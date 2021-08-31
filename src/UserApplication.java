import java.util.Scanner;

public class UserApplication {

    public static void main(String[] param) {
        Scanner scanner = new Scanner(System.in);
        String action, requestCode, secondaryCode = "";
        int testDuration = 4, timeBetweenTraces = 10;
        VirtualModem virtualModem;

        System.out.println("Welcome!");
        do { //Ask for action {E, M, G, P}
            System.out.println("Choose action by pressing the corresponding letters:");
            System.out.println("\t<E> Echo request response time test.");
            System.out.println("\t<M> Download and save image without errors.");
            System.out.println("\t<G> Download and save image with errors.");
            System.out.println("\t<P> Download and save image with GPS traces.");
            System.out.println("\t<Q> ARQ Test.");
            action = scanner.nextLine();
        } while(!action.equals("E") && !action.equals("M") && !action.equals("G") && !action.equals("P") && !action.equals("Q")); //Repeat if invalid action is given

        System.out.println("Write the request code (without special chars).");
        requestCode = scanner.nextLine();

        switch (action){ //Secondary info needed for some cases.
            case "E":
                System.out.println("Duration of test? (m)");
                testDuration = (new Scanner(System.in)).nextInt();
                break;
            case "P":
                System.out.println("Follow-up code (without special chars).");
                secondaryCode = (new Scanner(System.in)).nextLine();
                System.out.println("Time between traces? (s)");
                timeBetweenTraces = (new Scanner(System.in)).nextInt();
                break;
            case "Q":
                System.out.println("NACK code (without special chars).");
                secondaryCode = (new Scanner(System.in)).nextLine();
                System.out.println("Duration of test? (m)");
                testDuration = (new Scanner(System.in)).nextInt();
                break;
        }

        System.out.println("Set virtual modem speed. (kbps)");
        virtualModem = new VirtualModem((new Scanner(System.in)).nextInt(), 2000);

        virtualModem.read("\r\n\n\n"); //Read welcoming message from Ithaki

        switch (action){ //Start chosen action
            case "E":
                virtualModem.echoPacketTest(requestCode+"\r", testDuration * 1000 * 60);
                break;
            case "M":
                virtualModem.write(requestCode+"\r");
                virtualModem.saveImage("E1");
                break;
            case "G":
                virtualModem.write(requestCode+"\r");
                virtualModem.saveImage("E2");
                break;
            case "P":
                virtualModem.write(requestCode + secondaryCode + "\r");
                virtualModem.write(requestCode + virtualModem.getGpsTraces(timeBetweenTraces) + "\r");
                virtualModem.saveImage("M1");
                break;
            case "Q":
                virtualModem.arqTest(requestCode+"\r", secondaryCode+"\r", testDuration * 1000 * 60);
                break;
        }

        // Save log and close modem
        virtualModem.writeLogFile();
        virtualModem.close();
    }

}
