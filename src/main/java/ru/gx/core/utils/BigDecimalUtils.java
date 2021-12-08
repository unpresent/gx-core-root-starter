package ru.gx.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Вспомогательные утилиты для работы с BigDecimal
 */
@SuppressWarnings("unused")
public abstract class BigDecimalUtils {
    /**
     *
     * @param value значение, проверяемое на null.
     * @param defaultValue значение, которое будет возращено, если value == null.
     * @return value, если value не null. Иначе defaultValue.
     */
    @NotNull
    public static BigDecimal isNull(@Nullable final BigDecimal value, @NotNull final BigDecimal defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * @param mainValue возвращаемое занчение, если не совпадает с ifValue.
     * @param ifValue значение, с которым сверяется mainValue.
     * @return null, если #mainValue.equals(ifValue). Иначе mainValue.
     */
    @Nullable
    public static BigDecimal nullIf(@Nullable final BigDecimal mainValue, @Nullable final BigDecimal ifValue) {
        if (mainValue == null) {
            return null;
        }
        return mainValue.equals(ifValue) ? null : mainValue;
    }
}