package stefanowicz.kacper.menu;

import com.github.javafaker.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.model.Client;
import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.model.Rate;
import stefanowicz.kacper.model.enums.ProductCategory;
import stefanowicz.kacper.services.CurrencyService;
import stefanowicz.kacper.services.ShoppingService;
import stefanowicz.kacper.utils.UserDataService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuService {

    private final ShoppingService shoppingService;
    private final CurrencyService currencyService;

    public MenuService(String clientsFileName, String preferencesFileName, String productsFileName){
        this.currencyService = new CurrencyService();
        this.shoppingService = new ShoppingService(clientsFileName, preferencesFileName, productsFileName, chooseRate());
    }

    private Rate chooseRate(){
        List<Rate> rates = currencyService.getRates();
        rates.add(Rate.builder().code("PLN").currency("polski złoty").mid(BigDecimal.valueOf(1)).build());
        int choice = printCurrencyMenu(rates);

        if(choice < 0 || choice > rates.size()){
            throw new AppException("There is no such option");
        }
        else if(choice == 0){
            UserDataService.close();
            System.out.println("See you soon!");
            System.exit(0);
        }

        return rates.get(choice - 1);
    }

    private void printRates(List<Rate> rates){
        var counter = new AtomicInteger(0);
        rates.forEach(rate -> System.out.println(counter.incrementAndGet() + ". " + rate.getCode()));
        System.out.println("0. Exit");
    }

    private int printCurrencyMenu(List<Rate> rates){
        System.out.println("Choose currency of product prices");
        printRates(rates);
        return UserDataService.getInt("Choose currency: ");
    }

    public void mainMenu(){
        int option;
        do{
            try{
                option = printMenu();
                switch (option){
                    case 1 ->  listClients();
                    case 2 -> option1();
                    case 3 -> option2();
                    case 4 -> option3();
                    case 5 -> option4();
                    case 6 -> option5();
                    case 7 -> option6();
                    case 8 -> option7();
                    case 0 -> {
                        UserDataService.close();
                        System.out.println("See you soon!");
                        System.exit(0);
                    }
                    default -> System.out.println("There is no such option!");
                }
            }
            catch(Exception e){
                System.out.println("--------------------------------------");
                System.out.println("-----------EXCEPTION------------------");
                System.out.println(e.getMessage());
                System.out.println("--------------------------------------");
            }
        }while(true);
    }

    private int printMenu(){
        System.out.println("1. Print shopping list");
        System.out.println("2. Customer that bought most products.");
        System.out.println("3. Customer that spent most money on shopping.");
        System.out.println("4. Products grouped by popularity.");
        System.out.println("5. Most popular product.");
        System.out.println("6. Least popular product.");
        System.out.println("7. Categories grouped by popularity.");
        System.out.println("8. Send email for each customer with their shopping list.");
        System.out.println("0. Exit");
        return UserDataService.getInt("Choose an option:");
    }

    private void listClients(){
        var shoppingList = shoppingService.getShoppingList();
        shoppingList.forEach((k, v) -> System.out.println(toJson(k) + " ==> " + toJson(v)));
    }

    private void option1(){
        Client boughtMost  = shoppingService.boughtMostProducts();
        System.out.println(toJson(boughtMost));
    }

    private void option2(){
        Client spentMostMoney = shoppingService.spentMostMoney();
        System.out.println(toJson(spentMostMoney));
    }

    private void option3(){
        var productsPopularity = shoppingService.productPopularity();
        productsPopularity.forEach((k, v) -> System.out.println(toJson(k) + " -> " + v));
    }

    private void option4(){
        Product mostPopular = shoppingService.mostPopularProduct();
        System.out.println(toJson(mostPopular));
    }

    private void option5(){
        Product leastProduct = shoppingService.leastPopularProduct();
        System.out.println(toJson(leastProduct));
    }

    private void option6(){
        Map<ProductCategory, Integer> categoryPopularity = shoppingService.categoryPopularity();
        System.out.println(toJson(categoryPopularity));
    }

    private void option7(){
        shoppingService.sendEmailWithShopping();
    }

    private static <T> String toJson(T t){
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(t);
        }
        catch ( Exception e ){
            throw new AppException("to json conversion exception in menu service");
        }
    }
}
