
package com.kalsym.product.service.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author FaisalHayatJadoon
 */

public enum DeliveryType {
    SCHEDULED("Scheduled", "scheduled", "SCHEDULED"),
    ADHOC("AD-HOC", "Ad-Hoc", "ADHOC", "AD-HOC", "ad-hoc"),
    SELF("Self", "SELF", "self");
    
    static final private Map<String, DeliveryType> ALIAS_MAP = new HashMap<String, DeliveryType>();

    static {
        for (DeliveryType deliveryType : DeliveryType.values()) {
            
            ALIAS_MAP.put(deliveryType.name(), deliveryType);
            deliveryType.aliases.forEach(alias -> {
                ALIAS_MAP.put(alias, deliveryType);
            });
        }
    }

    static public boolean has(String value) {
        
        return ALIAS_MAP.containsKey(value);
    }

    static public DeliveryType fromString(String value) {
        if (value == null) {
            throw new NullPointerException("alias null");
        }
        DeliveryType deliveryType = ALIAS_MAP.get(value);
        if (deliveryType == null) {
            throw new IllegalArgumentException("Not an alias: " + value);
        }
        return deliveryType;
    }

    private List<String> aliases;

    private DeliveryType(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }
}
