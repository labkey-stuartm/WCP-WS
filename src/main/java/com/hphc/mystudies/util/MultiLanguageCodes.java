package com.hphc.mystudies.util;

import java.util.HashMap;
import java.util.Map;

public enum MultiLanguageCodes {
  ENGLISH("en", "English"),
  SPANISH("es", "Spanish"),
  RUSSIAN("ru", "Russian");

  private final String key;
  private final String value;
  private static boolean initialized = false;
  private static final Map<String, String> map = new HashMap<>();

  MultiLanguageCodes(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public static String getValue(String key) {
    return getMap().get(key);
  }

  public static String getKey(String value) {
    for (String string : getMap().keySet()) {
      if (value.equals(getMap().get(string))) return string;
    }
    return "";
  }

  public static Map<String, String> getMap() {
    if (!initialized) {
      for (MultiLanguageCodes s : MultiLanguageCodes.values()) {
        map.put(s.key, s.value);
      }
      initialized = true;
    }
    return map;
  }
}
