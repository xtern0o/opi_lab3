package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Это JSF-приложение");
        System.out.println("А это Main-класс заглушка, чтобы jar был исполняемым");

        System.out.println("Аргументы запуска:");
        for (String arg : args) {
            System.out.println("- " + arg);
        }
    }
}
