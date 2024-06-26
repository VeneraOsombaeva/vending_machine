import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.Scanner;

public class AppRunner {

    private final UniversalArray<Product> products = new UniversalArrayImpl<>();

    private final PaymentMethod coinAcceptor;
    private final PaymentMethod moneyReceiver;

    private static boolean isExit = false;

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
        coinAcceptor = new CoinAcceptor(100);
        moneyReceiver = new MoneyReceiver(200);
    }

    public static void run() {
        AppRunner app = new AppRunner();
        while (!isExit) {
            app.startSimulation();
        }
    }

    private void startSimulation() {
        print("В автомате доступны:");
        showProducts(products);

        choicePaymentMethod();
    }

    private UniversalArray<Product> getAllowedProducts(PaymentMethod paymentMethod) {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (paymentMethod.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private void chooseAction(UniversalArray<Product> products, PaymentMethod paymentMethod) {
        print(" a - Пополнить баланс");
        showActions(products);
        print(" h - Выйти");
        String action = fromConsole().substring(0, 1);
        if ("a".equalsIgnoreCase(action)) {
            paymentMethod.setAmount(paymentMethod.getAmount() + 10);
            print("Вы пополнили баланс на 10");
            return;
        }
        try {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                    paymentMethod.setAmount(paymentMethod.getAmount() - products.get(i).getPrice());
                    print("Вы купили " + products.get(i).getName());
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            if ("h".equalsIgnoreCase(action)) {
                isExit = true;
            } else {
                print("Недопустимая буква. Попрбуйте еще раз.");
                chooseAction(products, paymentMethod);
            }
        }


    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
    private void choicePaymentMethod() {
        print("Выберите способ оплаты: \n1 - Оплата монетами \n2 - Оплата банкнотами");
        int actionNumber = 0;
        try {
            actionNumber = Integer.parseInt(fromConsole());
        } catch (NumberFormatException e){
            print("Некорректный символ, введите снова");
            choicePaymentMethod();
        }

        PaymentMethod paymentMethod;
        switch (actionNumber){
            case 1:
                print("Монет на сумму: " + coinAcceptor.getAmount());

                paymentMethod = coinAcceptor;
                UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
                allowProducts.addAll(getAllowedProducts(paymentMethod).toArray());
                chooseAction(allowProducts, paymentMethod);
                break;
            case 2:
                print("Банкнот на сумму: " + moneyReceiver.getAmount());

                paymentMethod = moneyReceiver;
                UniversalArray<Product> allowProducts2 = new UniversalArrayImpl<>();
                allowProducts2.addAll(getAllowedProducts(paymentMethod).toArray());
                chooseAction(allowProducts2, paymentMethod);
                break;
            default:
                print("Выбранного продукта не существует!");
        }
    }
}
