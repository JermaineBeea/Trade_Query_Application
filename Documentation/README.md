# Trade Dynamics

**Author:** Tebagano Beea  
**Date:** September 09, 2025

## Introduction

A trade can be described as a sequence of conversions: from a primary commodity to a secondary commodity, and back again. The profitability of this trade depends on two factors: the opening conversion rate and the closing conversion rate. The net return is the traded quantity multiplied by the product of these rates, minus the initial quantity.

In financial markets, these conversion rates correspond to the sell rate and buy rate, with their difference defined as the spread. Since the buy rate always exceeds the sell rate, each trade begins with an immediate loss, making the spread a central determinant of profitability.

This paper develops a general framework for calculating trade return and profit factors in terms of sell rate, buy rate, spread, and market rate. The model is further extended to include trades involving intermediate commodities, where profit may be expressed in an alternate trade currency.

## Basic Trade Framework

### Trade Structure

Consider a trade consisting of a conversion from a primary commodity P to a secondary commodity Q, then back from the secondary commodity to the primary commodity.

The opening conversion factor is the scale used to convert the primary commodity to the secondary commodity. The closing conversion factor is the scale used to convert the secondary commodity back to the primary commodity.

The return on trade is equal to the quantity of the primary commodity traded, multiplied by the product of the conversion factors:

```
Trade Return = Quantity_P × Opening Rate × Closing Rate
```

The return profit/loss (expressed in units of the primary commodity) is:

```
Return Profit_P = Quantity_P × (Opening Rate × Closing Rate - 1)
```

### Trading Platform Structure

On a trading platform, each trade involves a base commodity and a quote commodity. For example, in the USD/ZAR market, USD is the base commodity and ZAR is the quote commodity.

There are two trade actions: selling and buying. Selling refers to converting the base commodity into the quote commodity, and buying refers to converting the quote commodity into the base commodity.

Two rates are used for selling and buying: the buy execution rate and the sell execution rate. Both rates are expressed as the quantity of the quote commodity per one unit of the base commodity.

The sell rate is used when converting primary to secondary. The buy rate is used when converting secondary to primary. The buy rate is always greater than the sell rate, and the difference between the two is called the spread:

```
Buy Rate = Sell Rate + Spread
```

## Generalized Profit Analysis

### Conversion Factors

These trade actions can be understood as specific cases of a general trade from commodity A to commodity B, and vice versa.

**Selling:** Converting the primary into the secondary commodity.
- Opening conversion factor = Sell Rate
- Closing conversion factor = 1 / Buy Rate

**Buying:** Converting the secondary commodity into the primary commodity.
- Opening conversion factor = 1 / Buy Rate
- Closing conversion factor = Sell Rate

### Universal Profit Factor

The generalized profit factor for both trade actions is:

```
Profit Factor = (Sell Rate / Buy Rate) - 1
```

Therefore, the generalized return profit/loss is:

```
Return Profit = Quantity_P × ((Sell Rate / Buy Rate) - 1)
```

### Spread Relationship

The buy rate being greater than the sell rate can be derived as follows: For any executed trade, there is an immediate trade loss incurred. The immediate loss is the consequence of the spread and is calculated as the loss incurred when a trade is opened and closed instantaneously at the time of execution.

The profit factor must be less than zero; therefore:

```
Sell Rate / Buy Rate < 1  →  Sell Rate < Buy Rate
```

## Market Rate Framework

### Market Rate Definition

The market rate ("actual rate") at a specific time is the median between the buy rate and sell rate. It can be determined as:

```
Market Rate = (Sell Rate + Buy Rate) / 2
```

This can be expressed in alternative forms:

```
Market Rate = Sell Rate + (1/2) × Spread
Market Rate = Buy Rate - (1/2) × Spread
```

Rearranging these relationships:

```
Sell Rate = Market Rate - (1/2) × Spread
Buy Rate = Market Rate + (1/2) × Spread
```

### Profit in Terms of Market Rate

The return profit/loss expressed in terms of the market rate becomes:

```
Return Profit = Quantity_P × (((2 × Market Rate - Spread) / (2 × Market Rate + Spread)) - 1)
```

## Multi-Commodity Trading

### Intermediate Commodity Framework

A general trade may involve an intermediate commodity A. For example, converting 90.87 ZAR to 5 USD, then trading 5 USD for EUR.

The return profit in an alternate currency (trade profit currency, N) is calculated through the following relationships:

```
Quantity_P = Trade Amount_A × Rate_AP
Trade Profit_N = Return Profit_P × Rate_PN
```

### Complete Multi-Currency Solution

Combining these relationships with the profit factor:

```
Trade Profit_N = Trade Amount_A × Rate_AP × Rate_PN × ((Sell Rate / Buy Rate) - 1)
```

In terms of market rates:

```
Trade Profit_N = Trade Amount_A × Rate_AP × Rate_PN × (((2 × Market Rate - Spread) / (2 × Market Rate + Spread)) - 1)
```

**Note:** This framework assumes all conversion rates are available and that transaction costs beyond the spread are negligible.

---

## Key Formulas Summary

| Concept | Formula |
|---------|---------|
| Buy Rate | `Sell Rate + Spread` |
| Market Rate | `(Sell Rate + Buy Rate) / 2` |
| Profit Factor | `(Sell Rate / Buy Rate) - 1` |
| Return Profit | `Quantity_P × Profit Factor` |
| Multi-Currency Profit | `Trade Amount_A × Rate_AP × Rate_PN × Profit Factor` |
