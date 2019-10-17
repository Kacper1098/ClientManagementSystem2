package stefanowicz.kacper.main;

import stefanowicz.kacper.menu.DataMenuService;
import stefanowicz.kacper.menu.MenuService;

public class App {
    public static void main( String[] args ) {
        final String CLIENTS_FILENAME = "files/clients.json";
        final String PREFERENCES_FILENAME = "files/preferences.json";
        final String PRODUCTS_FILENAME = "files/products.json";
        var dataMenu = new DataMenuService();
        dataMenu.dataMenu();

        var menu = new MenuService(CLIENTS_FILENAME, PREFERENCES_FILENAME , PRODUCTS_FILENAME);
        menu.mainMenu();
    }
}
