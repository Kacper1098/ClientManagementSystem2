package stefanowicz.kacper.utils;

import stefanowicz.kacper.exceptions.AppException;

import java.util.Scanner;

public final class UserDataService {

    private static Scanner scn  = new Scanner(System.in);

    private UserDataService(){};

    public static int getInt(String message){
        System.out.println(message);

        String text = scn.nextLine();

        if(!text.matches("\\d+")){
            throw new AppException("This is not int value");
        }

        return Integer.parseInt(text);
    }

    public static void close(){
        if(scn != null){
            scn.close();
            scn = null;
        }
    }

}
