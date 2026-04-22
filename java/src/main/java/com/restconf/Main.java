package com.restconf;

import com.restconf.client.CrudResponse;
import com.restconf.client.RestconfClient;
import com.restconf.model.NetworkInterface;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RestconfClient client = new RestconfClient("http://localhost:8080", "admin", "admin");

        NetworkInterface iface = new NetworkInterface(
                "Loopback100",
                "Demo interface",
                "iana-if-type:softwareLoopback",
                true,
                "10.10.10.1",
                32
        );

        CrudResponse response;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Press [Enter] to execute CREATE (POST)...");
        scanner.nextLine();
        response = client.createInterface(iface);
        printResponse("CREATE (POST)", response);

        // Uncomment one at a time during demo
        System.out.println("Press [Enter] to execute READ (GET)...");
        scanner.nextLine();
        response = client.getInterface("Loopback100");
        printResponse("READ (GET)", response);

        System.out.println("Press [Enter] to execute UPDATE (PUT)...");
        scanner.nextLine();
        iface.setDescription("Updated loopback interface");
        iface.setEnabled(false);
        response = client.updateInterface("Loopback100", iface);
        printResponse("UPDATE (PUT)", response);

        System.out.println("Press [Enter] to execute DELETE (DELETE)...");
        scanner.nextLine();
        response = client.deleteInterface("Loopback100");
        printResponse("DELETE (DELETE)", response);

        // Terminate the JVM to prevent OkHttp background threads from keeping it alive
        System.exit(0);
    }

    private static void printResponse(String title, CrudResponse response) {
        System.out.println("===== " + title + " =====");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Success: " + response.isSuccess());
        System.out.println("Body: " + response.getBody());
        System.out.println();
    }
}
