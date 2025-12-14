package co.za.Main.TradeModules;

import java.math.BigDecimal;
import java.math.RoundingMode;
public class Trade_Function {

    BigDecimal spread;
    BigDecimal rateBK;
    BigDecimal rateKN;
    BigDecimal tradeAmount;
    BigDecimal opening_factor;
    BigDecimal closing_factor;
    BigDecimal opening_value;
    BigDecimal closing_value;
    TradeAction action;
    boolean basedOnMarketRate = false; // Default to false (execution-based)


    // For basedOnMarketRate = false (default), sell/Buy variable is based on execution rate
    // SellExecution rate = marketSellRate - (spread/2)
    // BuyExecution rate = marketBuyRate + (spread/2)
    // basedOnMarketRate = true, sell/Buy variable is based on market rate
    // Execution (Be it sell or buy) rate is the ratio of the qoute commodity to 1 unit of the base commodity within a trade
    // Trade calculation us ethe opening and closing rates adjusted for spread if basedOnMarketRate is false

    public Trade_Function(
        TradeAction action, BigDecimal spread,
        BigDecimal rateBK, BigDecimal rateKN, BigDecimal tradeAmount,
        BigDecimal opening_value, BigDecimal closing_value){
        
        this.tradeAmount = tradeAmount;
        this.opening_value = opening_value;
        this.closing_value = closing_value;
        this.action = action;
        this.spread = spread;
        this.rateBK = rateBK;
        this.rateKN = rateKN;
        this.basedOnMarketRate = false; // Default to execution-based
        
        zero_check();
        run_trade_action();
        }

    public void zero_check(){
        if(action == TradeAction.SELL){
            if(closing_value.compareTo(BigDecimal.ZERO) == 0){
                throw new ArithmeticException("Closing execution rate cannot be zero for SELL action");
            }

        } else if (action == TradeAction.BUY){
            if(opening_value.compareTo(BigDecimal.ZERO) == 0){
                throw new ArithmeticException("Opening execution rate cannot be zero for BUY action");
            }
        }
    }

    public void set_default_rates(){
        if(action == TradeAction.SELL){
            rateBK = BigDecimal.ONE;
            rateKN = BigDecimal.ONE;
        } else if (action == TradeAction.BUY){
            rateBK = opening_value;
            rateKN = BigDecimal.ONE.divide(closing_value, 10, RoundingMode.HALF_UP);
        }
    }


    public void run_trade_action(){
        if (action == TradeAction.SELL) {
            // opening_value is the opening sell rate
            // closing_value is the closing buy rate
            BigDecimal adjOpen = !basedOnMarketRate
                    ? opening_value
                    : opening_value.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
            BigDecimal adjClose = !basedOnMarketRate
                    ? closing_value
                    : closing_value.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));

            this.opening_value = adjOpen;
            this.closing_value = adjClose;

            this.opening_factor = this.opening_value;
            this.closing_factor = BigDecimal.ONE.divide(this.closing_value, 10, RoundingMode.HALF_UP);

        } else if (action == TradeAction.BUY) {
            // opening_value is the opening buy rate
            // closing_value is the closing sell rate
            BigDecimal adjOpen = !basedOnMarketRate
                    ? opening_value
                    : opening_value.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
            BigDecimal adjClose = !basedOnMarketRate
                    ? closing_value
                    : closing_value.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));

            this.opening_value = adjOpen;
            this.closing_value = adjClose;

            this.opening_factor = BigDecimal.ONE.divide(this.opening_value, 10, RoundingMode.HALF_UP);
            this.closing_factor = this.closing_value;
        } else {
            throw new IllegalArgumentException("Unsupported TradeAction: " + action);
        }

    }

    public void setBasedOnMarketRate(boolean basedOnMarketRate) {
        this.basedOnMarketRate = basedOnMarketRate;
    }

    public boolean isBasedOnMarketRate() {
        return basedOnMarketRate;
    }

    public BigDecimal returnProfit(BigDecimal tradeAmount) {
        return tradeAmount.multiply(rateBK).multiply(rateKN).multiply((opening_factor.multiply(closing_factor)).subtract(BigDecimal.ONE));
    }

    public BigDecimal returnProfitFactor(BigDecimal tradeProfit, BigDecimal tradeAmount) {
        return tradeProfit.divide(tradeAmount.multiply(rateBK).multiply(rateKN), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnTradeAmount(BigDecimal tradeProfit, BigDecimal tradeAmount){
        return tradeProfit.divide(rateBK.multiply(rateKN).multiply((opening_factor.multiply(closing_factor)).subtract(BigDecimal.ONE)), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnOpening(BigDecimal tradeProfit, BigDecimal tradeAmount) {
        BigDecimal variable_a = tradeProfit.divide(tradeAmount.multiply(rateBK).multiply(rateKN), 10, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal variable_b = variable_a.divide(closing_factor, 10, RoundingMode.HALF_UP);
        if(action == TradeAction.SELL){
            return variable_b;
        } else {
            return BigDecimal.ONE.divide(variable_b, 10, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal returnClosing(BigDecimal tradeProfit, BigDecimal tradeAmount) {
        BigDecimal variable_a = tradeProfit.divide(tradeAmount.multiply(rateBK).multiply(rateKN), 10, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal variable_b = variable_a.divide(opening_factor, 10, RoundingMode.HALF_UP);
        if(action == TradeAction.SELL){
            return BigDecimal.ONE.divide(variable_b, 10, RoundingMode.HALF_UP);
        } else {
            return variable_b;
        }
    }
}