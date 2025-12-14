package co.za.Main.TradeModules;

public enum TradeAction {
    SELL, BUY, NO_TRADE
}

class UsageMain {

    public static void main(String[] args) {

        TradeAction action = TradeAction.SELL;

        if (action == TradeAction.SELL) {
            System.out.println("Trade action is sell");
        }
    }
}
