package info.kgeorgiy.ja.shinkareva.bank;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;

/**
 * Server class.
 * @author Shinkareva Alyona (alyona.i.shinkareva@gmail.com)
 */

public final class Server {
    private static final int DEFAULT_BANK_PORT = 8888;
    private static final int DEFAULT_REGISTRY_PORT = 1099;
    private static final String HOST_NAME = "//localhost/bank";

    /**
     * Main function for launching server.
     * @param args no arguments.
     */

    public static void main(final String... args) throws RemoteException {
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_BANK_PORT;
        Registry registry = LocateRegistry.createRegistry(DEFAULT_REGISTRY_PORT);
        final Bank bank = new RemoteBank(port);
        try {
            UnicastRemoteObject.exportObject(bank, port);
            Naming.rebind(HOST_NAME, bank);
            System.out.println("Server started");
        } catch (final RemoteException e) {
            System.out.println("Cannot export object: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (final MalformedURLException e) {
            System.out.println("Malformed URL");
        }
    }
}
