package com.kevin.garden;

public class PhpAPI {

    private static String server_url = "http://192.168.1.12/";
    private static String login = "garden_api/login.php";
    private static String register = "garden_api/register.php";


    public String LOGIN(){ return server_url + login; }
    public String REGISTER(){ return server_url + register; }

}
