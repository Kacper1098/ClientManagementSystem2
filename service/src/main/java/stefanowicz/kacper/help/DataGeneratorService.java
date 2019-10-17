package stefanowicz.kacper.help;

import com.github.javafaker.Faker;
import stefanowicz.kacper.converter.ClientJsonConverter;
import stefanowicz.kacper.converter.ProductsWithQuantityConverter;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.model.Client;
import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.model.ProductWithQuantity;
import stefanowicz.kacper.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public final class DataGeneratorService {
    private final Faker faker = new Faker();
    private final String PRODUCTS_FILE = "files/random/products.json";
    private final String CATEGORIES_FILE = "files/random/categories.json";

    public void generateNewFiles(int numberOfClients, int numberOfProducts){
        Set<Client> clients = new HashSet<>();
        var clientsConverter = new ClientJsonConverter("files/clients.json");
        for (int i = 0; i < numberOfClients; i++) {
            clients.add(generateClient());
        }
        clientsConverter.toJson(clients);
        Set<ProductWithQuantity> products = new HashSet<>();
        var productsConverter = new ProductsWithQuantityConverter("files/products.json");
        for (int i = 0; i < numberOfProducts; i++) {
            products.add(generateProduct());
        }
        productsConverter.toJson(products);
        System.out.println("----- DATA GENERATED SUCCESSFULLY -----");
    }

    private static String randomEmailDomain(){
        final String[] arr = {"yahoo.com", "hotmail.com" ,"gmail.com", "comcast.net", "msn.com", "aol.com", "ntlworld.com"};
        return arr[new Random().nextInt(arr.length)];
    }

    private  Client generateClient(){
        Random rnd = new Random();
        BigDecimal cash = new BigDecimal(Math.random()).multiply(new BigDecimal(5000)).setScale(1, RoundingMode.HALF_DOWN);
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        return Client
                .builder()
                .firstName(firstName)
                .lastName(lastName)
                .age(rnd.nextInt(58) + 18)
                .cash(cash)
                .preferences(generatePreferences())
                .email(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + randomEmailDomain())
                .build();
    }

    private  String generatePreferences(){

        StringBuilder preferences = new StringBuilder();
        for (int i = 1; i <= ProductCategory.values().length; i++) {
            preferences.append(i);
        }
        List<Character> preferenceList = preferences
                                    .chars()
                                    .mapToObj(c -> (char) c)
                                    .collect(Collectors.toList());

        Collections.shuffle(preferenceList);

        return preferenceList.stream().map(String::valueOf).collect(Collectors.joining());
    }

    private  ProductCategory getRandomCategory(){
        var converter = new ProductCategoriesJsonConverter(CATEGORIES_FILE);
        List<ProductCategory> categories = converter.fromJson().orElseThrow(() -> new AppException("Error while converting categories from json"));

        return categories.get(new Random().nextInt(categories.size()));
    }

    private  String getProductNameFromCategory(ProductCategory category){
        var converter = new CategoriesWithProductsJsonConverter(PRODUCTS_FILE);
        List<CategoryWithProducts> categoriesWithProducts = converter
                .fromJson()
                .orElseThrow(() -> new AppException("Error while converting categories with products from json"));
        CategoryWithProducts cwp1 = categoriesWithProducts
                .stream()
                .filter(cwp -> cwp.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new AppException("Error while filtering categories with products"));

        return cwp1.getProducts().get(new Random().nextInt(cwp1.getProducts().size()));
    }

    private  BigDecimal getPriceByCategory(ProductCategory category){
        BigDecimal price;
        switch (category){
            case AGD -> price =  new BigDecimal(Math.random()).multiply(new BigDecimal(2000)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(1000));
            case FOOD -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(5)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(2));
            case PETS -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(30)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(15));
            case BOOKS -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(40)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(20));
            case CLOTHES -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(200)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(70));
            case FURNITURE -> price = new BigDecimal(Math.random()).multiply(new BigDecimal(1000)).setScale(1, RoundingMode.HALF_DOWN).add(new BigDecimal(200));
            default -> throw new AppException("Could not find given product category " + category);
        }
        return price;
    }

    private  int getProductQuantity(ProductCategory category){
        int quantity;
        Random rnd = new Random();
        switch (category){
            case AGD, BOOKS -> quantity = 1;
            case FOOD -> quantity = rnd.nextInt(5) + 1;
            case FURNITURE, PETS -> quantity = rnd.nextInt(3) + 1;
            case CLOTHES -> quantity = rnd.nextInt(4 ) + 1;
            default -> throw new AppException("Could not find given product category " + category);
        }
        return quantity;
    }

    private  ProductWithQuantity generateProduct(){
        ProductCategory category = getRandomCategory();
        return ProductWithQuantity
                .builder()
                .product(
                        Product
                        .builder()
                        .category(category)
                        .name(getProductNameFromCategory(category))
                        .price(getPriceByCategory(category))
                        .build()
                )
                .quantity(getProductQuantity(category))
                .build();
    }
}
