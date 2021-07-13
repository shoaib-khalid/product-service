
package com.kalsym.product.service.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author FaisalHayatJadoon
 */
public enum StorePaymentType {
    COD(""),
    ONLINEPAYMENT("");
    
    static final private Map<String, StorePaymentType> ALIAS_MAP = new HashMap<String, StorePaymentType>();

    static {
        for (StorePaymentType storePaymentType : StorePaymentType.values()) {
            
            ALIAS_MAP.put(storePaymentType.name(), storePaymentType);
            storePaymentType.aliases.forEach(alias -> {
                ALIAS_MAP.put(alias, storePaymentType);
            });
        }
    }

    static public boolean has(String value) {
        
        return ALIAS_MAP.containsKey(value);
    }

    static public StorePaymentType fromString(String value) {
        if (value == null) {
            throw new NullPointerException("alias null");
        }
        StorePaymentType storePaymentType = ALIAS_MAP.get(value);
        if (storePaymentType == null) {
            throw new IllegalArgumentException("Not an alias: " + value);
        }
        return storePaymentType;
    }

    private List<String> aliases;

    private StorePaymentType(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }
}
