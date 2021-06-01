package com.qapital

import java.math.BigDecimal
import java.math.RoundingMode

const val CURRENCY_SCALE = 2;

typealias Id = Long

typealias Amount = BigDecimal

fun Amount.enforceScale(): Amount = setScale(CURRENCY_SCALE, RoundingMode.HALF_EVEN)
fun Amount.isPositive() = this > BigDecimal.ZERO
fun Amount.isNegative() = this < BigDecimal.ZERO

fun amountOf(value: Number): Amount =
    when (value) {
        is Float -> value.toBigDecimal()
        is Double -> value.toBigDecimal()
        else -> value.toLong().toBigDecimal()
    }.enforceScale()
