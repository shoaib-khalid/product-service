package com.kalsym.product.service.utility;

import org.slf4j.LoggerFactory;

/**
 *
 * @author Sarosh
 */
public class Logger {

    public static final org.slf4j.Logger application = LoggerFactory.getLogger("application");

    public static String pattern = "[v{}][{}] {}";
}
