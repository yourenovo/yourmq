
package org.yourmq.common;

public enum Feature {
    QuoteFieldNames,
    OrderedField,
    WriteClassName,
    WriteArrayClassName,
    WriteDateUseTicks,
    WriteDateUseFormat,
    WriteBoolUse01,
    WriteSlashAsSpecial,
    WriteNumberUseString,
    ParseIntegerUseLong,
    BrowserSecure,
    BrowserCompatible,
    TransferCompatible,
    EnumUsingName,
    StringNullAsEmpty,
    BooleanNullAsFalse,
    NumberNullAsZero,
    ArrayNullAsEmpty,
    StringFieldInitEmpty,
    SerializeNulls,
    SerializeMapNullValues,
    UseSingleQuotes,
    UseSetter,
    UseOnlySetter,
    UseGetter,
    UseOnlyGetter,
    DisThreadLocal,
    StringJsonToNode,
    StringDoubleToDecimal,
    PrettyFormat,
    DisableClassNameRead,
    DisableCollectionDefaults;

    public final int code = 1 << this.ordinal();

    private Feature() {
    }

    public static boolean isEnabled(int features, Feature feature) {
        return (features & feature.code) != 0;
    }

    public static int config(int features, Feature feature, boolean enable) {
        if (enable) {
            features |= feature.code;
        } else {
            features &= ~feature.code;
        }

        return features;
    }

    public static int of(Feature... features) {
        if (features == null) {
            return 0;
        } else {
            int value = 0;
            Feature[] var2 = features;
            int var3 = features.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Feature feature = var2[var4];
                value |= feature.code;
            }

            return value;
        }
    }
}
