package stefanowicz.kacper.services;

import stefanowicz.kacper.converter.ClientJsonConverter;
import stefanowicz.kacper.converter.PreferenceJsonConverter;
import stefanowicz.kacper.converter.ProductsWithQuantityConverter;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.help.EmailService;
import stefanowicz.kacper.model.*;
import stefanowicz.kacper.model.enums.ProductCategory;
import stefanowicz.kacper.validators.ClientValidator;
import stefanowicz.kacper.validators.PreferenceValidator;
import stefanowicz.kacper.validators.ProductValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


import static j2html.TagCreator.*;

public class ShoppingService {
   private final Map<Client, Set<ProductWithQuantity>> shoppingList;

   public ShoppingService(String clientsFileName, String preferencesFileName, String productsFileName, Rate rate) {
       shoppingList = getData(clientsFileName, preferencesFileName, productsFileName, rate);
   }

   public Map<Client, Set<ProductWithQuantity>> getShoppingList(){
       return this.shoppingList;
   }

    /**
     *
     * @param clientsFileName Name of file with clients
     * @param preferencesFileName Name of file with preferences
     * @param productsFileName Name of file with products
     * @return Map with customer as key and set of products bought by this customer
     */
   private Map<Client, Set<ProductWithQuantity>> getData(String clientsFileName, String preferencesFileName, String productsFileName, Rate rate){
       if(clientsFileName == null){
           throw new AppException("Clients file name is null");
       }
       if(preferencesFileName == null){
           throw new AppException("Preferences file name is null");
       }
       if(productsFileName == null){
           throw new AppException("Products file name is null");
       }

       Map<Client, Set<ProductWithQuantity>> shoppingMap = new HashMap<>();

       var preferences = validatePreferences(preferencesFileName);
       var products = calculateProductPrices(validateProducts(productsFileName), rate);
       var clients = validateClients(clientsFileName);

       Map<Integer, List<ProductWithQuantity>> productsByPreference = getProductsByPreference(preferences, products);

       clients.forEach(client -> shoppingMap.put(client, buyProducts(client, productsByPreference)));

        return shoppingMap;
   }

    /**
     *
     * @param products Set of products
     * @param rate Rate chosen by user
     * @return Set of products with prices in chosen rate
     */
   private Set<ProductWithQuantity> calculateProductPrices(Set<ProductWithQuantity> products, Rate rate){
       if(products == null){
           throw new AppException("ShoppingService exception - calculateProductPrices method - products given as argument are null");
       }
       return products
               .stream()
               .peek(productWithQuantity ->
                       productWithQuantity
                               .getProduct()
                               .setPrice(
                                       productWithQuantity
                                               .getProduct()
                                               .getPrice()
                                               .divide(rate.getMid(), 2, RoundingMode.HALF_UP)))
               .collect(Collectors.toSet());
   }

   private Set<Client> validateClients(String clientsFileName){
       var clientConverter = new ClientJsonConverter(clientsFileName);
       var clientValidator = new ClientValidator();
       var clientCounter = new AtomicInteger(1);

       return clientConverter
               .fromJson()
               .orElseThrow(() -> new AppException("from json client conversion exception"))
               .stream()
               .filter(client -> {

                   var errors = clientValidator.validate(client);

                   if( clientValidator.hasErrors()){
                       System.out.println("--------------------------------------");
                       System.out.println("-- Validation error for client no. " + clientCounter.get() + " --");
                       System.out.println("--------------------------------------");
                       errors.forEach( ( k, v ) -> System.out.println(k + ": " + v) );
                   }
                   clientCounter.incrementAndGet();
                   return !clientValidator.hasErrors();

               }).collect(Collectors.toSet());

   }

   private Set<Preference> validatePreferences(String preferencesFileName){
       var preferenceConverter = new PreferenceJsonConverter(preferencesFileName);
       var preferenceValidator = new PreferenceValidator();
       var preferenceCounter = new AtomicInteger(1);

       return preferenceConverter
               .fromJson()
               .orElseThrow(() -> new AppException("from json prefernce conversion exception"))
               .stream()
               .filter(preference -> {

                   var errors = preferenceValidator.validate(preference);

                   if(preferenceValidator.hasErrors()){
                       System.out.println("--------------------------------------");
                       System.out.println("-- Validation error for preference no. " + preferenceCounter.get() + " --");
                       System.out.println("--------------------------------------");
                       errors.forEach( ( k, v ) -> System.out.println(k + ": " + v) );
                   }
                   preferenceCounter.incrementAndGet();

                   return !preferenceValidator.hasErrors();
               })
               .collect(Collectors.toSet());
   }

   private Set<ProductWithQuantity> validateProducts(String productsFileName){
       var productConverter = new ProductsWithQuantityConverter(productsFileName);
       var productValidator = new ProductValidator();
       var productCounter = new AtomicInteger(1);

       return productConverter
               .fromJson()
               .orElseThrow(() -> new AppException("from json products conversion exception"))
               .stream()
               .filter(productWithQuantity ->  {

                   var errors = productValidator.validate(productWithQuantity.getProduct());

                   if(productValidator.hasErrors()){
                       System.out.println("--------------------------------------");
                       System.out.println("-- Validation error for product no. " + productCounter.get() + " --");
                       System.out.println("--------------------------------------");
                       errors.forEach( ( k, v ) -> System.out.println(k + ": " + v) );
                   }
                   productCounter.incrementAndGet();
                   return !productValidator.hasErrors();
               })
               .collect(Collectors.toSet());
   }

    /**
     *
     * @param preferences Set of preferences
     * @param productWithQuantities et of products with quantity
     * @return Map with preference as key and list of products with category of this preference.
     */
   public Map<Integer, List<ProductWithQuantity>> getProductsByPreference(Set<Preference> preferences, Set<ProductWithQuantity> productWithQuantities){
        return productWithQuantities
               .stream()
               .collect(Collectors.groupingBy(productWithQuantity -> productWithQuantity.getProduct().getCategory()))
               .entrySet()
               .stream()
               .collect(Collectors.toMap(
                       e -> ( preferences
                               .stream()
                               .filter(preference -> preference.getProductCategory().equals(e.getKey()))
                               .findFirst()
                               .orElseThrow(() -> new AppException("Could not find preference number for given category"))
                               .getNumber() ),
                       e -> e
                               .getValue()
                               .stream()
                               .sorted(Comparator.comparing(product -> product.getProduct().getPrice().divide(new BigDecimal(product.getQuantity()), 4, RoundingMode.HALF_UP)))
                               .collect(Collectors.toList())
               ));
   }

    /**
     *
     * @param client Client that wants to buy products
     * @param productsByPreference Map with preference as key and list of products with category of this preference.
     * @return Set of products with quantity, bought by this customer.
     */
    private Set<ProductWithQuantity> buyProducts(Client client, Map<Integer, List<ProductWithQuantity>> productsByPreference){
       Set<ProductWithQuantity> boughtProducts = new HashSet<>();
        var ref = new Object() {
            BigDecimal clientsCash = client.getCash();
        };

        Set<Integer> numberOfPreferences = productsByPreference.keySet();

        for(char preference : client.getPreferences().toCharArray()){
            if(numberOfPreferences.contains(Character.getNumericValue(preference))){
                productsByPreference.get(Character.getNumericValue(preference)).forEach(productWithQuantity -> {
                    while(ref.clientsCash.compareTo(BigDecimal.ZERO) > 0){
                        if(productWithQuantity.getProduct().getPrice().multiply(new BigDecimal(productWithQuantity.getQuantity())).compareTo(ref.clientsCash) <= 0){
                            ref.clientsCash = ref.clientsCash
                                    .subtract(productWithQuantity.getProduct().getPrice().multiply(new BigDecimal(productWithQuantity.getQuantity())));
                            boughtProducts.add(productWithQuantity);
                        }
                        else{
                            break;
                        }
                    }
                });
            }
        }
        return boughtProducts;
    }

    /**
     *
     * @return Customer that bought the most products.
     */
    public Client boughtMostProducts() {
        return this.shoppingList
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    /**
     *
     * @return Customer that spent most money on shopping.
     */
    public Client spentMostMoney() {
        return this.shoppingList
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream()
                                .map(e1 -> e1.getProduct().getPrice().multiply(new BigDecimal(e1.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ))
                .entrySet()
                .stream()
                .sorted((e2, e3) -> e3.getValue().compareTo(e2.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new AppException("Error while looking for customer that spent most money"));
    }

    /**
     *
     * @return Map of product as key and number of purchases of that product
     */
    public Map<Product, Integer> productPopularity() {
        return this.shoppingList
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.groupingBy(ProductWithQuantity::getProduct, Collectors.summingInt(ProductWithQuantity::getQuantity)));
    }

    /**
     *
     * @return Product with the highest number of purchases.
     */
    public Product mostPopularProduct() {
        return this.shoppingList
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.groupingBy(ProductWithQuantity::getProduct, Collectors.summingInt(ProductWithQuantity::getQuantity)))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }


    /**
     *
     * @return Product with the lowest number of purchases.
     */
    public Product leastPopularProduct() {
        return this.shoppingList
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.groupingBy(ProductWithQuantity::getProduct, Collectors.summingInt(ProductWithQuantity::getQuantity)))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    /**
     *
     * @return Product category as key and number of purchases of this category as value, sorted by value in descending order.
     */
    public Map<ProductCategory, Integer> categoryPopularity()
    {
        return shoppingList
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.groupingBy(prw -> prw.getProduct().getCategory(), Collectors.summingInt(ProductWithQuantity::getQuantity)))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Sends eamil for each client with their products.
     */
    public void sendEmailWithShopping(){
        var emailService = new EmailService();
        this.shoppingList
                .forEach((client, productWithQuantities) ->
                        emailService.send(client.getEmail(), "Your  products", productToHtml(client, productWithQuantities)));
    }

    /**
     *
     * @param client
     * @param productWithQuantities Products to convert
     * @return Converts set of products to html table.
     */
    private String productToHtml(Client client, Set<ProductWithQuantity> productWithQuantities){
        if(productWithQuantities == null){
            throw new AppException("Products to html method exceptions - set of products is null");
        }
        return div(
                h2(client.getFirstName() + " " + client.getLastName() + " products"),
                table().with(
                        thead(
                                tr().with(
                                        th("Name"),
                                        th(" Price for each"),
                                        th("Category"),
                                        th("Quantity")
                                )
                        ),
                        tbody(
                                each(productWithQuantities, productWithQuantity -> tr(
                                        td(productWithQuantity.getProduct().getName()),
                                        td(productWithQuantity.getProduct().getPrice().toString()),
                                        td(productWithQuantity.getProduct().getCategory().toString()),
                                        td(Integer.toString(productWithQuantity.getQuantity()))
                                ))
                        )
                )
        ).render();
    }

}
