package co.za.Main.TradeModules;

import java.math.BigDecimal;
import java.math.RoundingMode;
public class Trade_Function {

    BigDecimal spread;
    BigDecimal ratePK;
    BigDecimal ratePN;
    BigDecimal tradeAmount;
    BigDecimal opening_factor;
    BigDecimal closing_factor;
    BigDecimal openingExecution;
    BigDecimal closingExecution;
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
        BigDecimal ratePK, BigDecimal ratePN, BigDecimal tradeAmount,
        BigDecimal openingExecution, BigDecimal closingExecution){
        
        this.tradeAmount = tradeAmount;
        this.openingExecution = openingExecution;
        this.closingExecution = closingExecution;
        this.action = action;
        this.spread = spread;
        this.ratePK = ratePK;
        this.ratePN = ratePN;
        this.basedOnMarketRate = false; // Default to execution-based
        
        zero_check();
        run_trade_action();
        
        }

    public void zero_check(){
        if(action == TradeAction.SELL){
            if(closingExecution.compareTo(BigDecimal.ZERO) == 0){
                throw new ArithmeticException("Closing execution rate cannot be zero for SELL action");
            }

        } else if (action == TradeAction.BUY){
            if(openingExecution.compareTo(BigDecimal.ZERO) == 0){
                throw new ArithmeticException("Opening execution rate cannot be zero for BUY action");
            }
        }
    }

    public void run_trade_action(){
        if (action == TradeAction.SELL) {
            // openingExecution is the opening sell rate
            // closingExecution is the closing buy rate
            BigDecimal adjOpen = basedOnMarketRate
                    ? openingExecution
                    : openingExecution.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
            BigDecimal adjClose = basedOnMarketRate
                    ? closingExecution
                    : closingExecution.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));

            this.openingExecution = adjOpen;
            this.closingExecution = adjClose;

            this.opening_factor = this.openingExecution;
            this.closing_factor = BigDecimal.ONE.divide(this.closingExecution, 10, RoundingMode.HALF_UP);

        } else if (action == TradeAction.BUY) {
            // openingExecution is the opening buy rate
            // closingExecution is the closing sell rate
            BigDecimal adjOpen = basedOnMarketRate
                    ? openingExecution
                    : openingExecution.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
            BigDecimal adjClose = basedOnMarketRate
                    ? closingExecution
                    : closingExecution.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));

            this.openingExecution = adjOpen;
            this.closingExecution = adjClose;

            this.opening_factor = BigDecimal.ONE.divide(this.openingExecution, 10, RoundingMode.HALF_UP);
            this.closing_factor = this.closingExecution;
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
        return tradeAmount.multiply(ratePK).multiply(ratePN)
                .multiply((opening_factor.multiply(closing_factor)).subtract(BigDecimal.ONE));
    }

    public BigDecimal returnProfitFactor(BigDecimal tradeProfit, BigDecimal tradeAmount) {
        return tradeProfit.divide(tradeAmount.multiply(ratePK).multiply(ratePN), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnTradeAmount(BigDecimal tradeProfit, BigDecimal tradeAmount){
        return tradeProfit.divide(tradeAmount.multiply(ratePK).multiply(ratePN).multiply((opening_factor.multiply(closing_factor)).subtract(BigDecimal.ONE)), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnOpening(BigDecimal tradeAmount, BigDecimal tradeProfit) {
        BigDecimal variable_a = tradeProfit.divide(tradeAmount.multiply(ratePK).multiply(ratePN), 10, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal variable_b = variable_a.divide(closing_factor, 10, RoundingMode.HALF_UP);
        if(action == TradeAction.SELL){
            return variable_b;
        } else {
            return BigDecimal.ONE.divide(variable_b, 10, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal returnClosing(BigDecimal tradeAmount, BigDecimal tradeProfit, BigDecimal sellVariable) {
        BigDecimal variable_a = tradeProfit.divide(tradeAmount.multiply(ratePK).multiply(ratePN), 10, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal variable_b = variable_a.divide(opening_factor, 10, RoundingMode.HALF_UP);
        if(action == TradeAction.SELL){
            return BigDecimal.ONE.divide(variable_b, 10, RoundingMode.HALF_UP);
        } else {
            return variable_b;
        }
    }
}