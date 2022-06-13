package com.example.techjobs.common.util;

import java.text.Normalizer;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;

public class Utils {

  /** Hàm tạo mã xác thực */
  public static String createVerifyCode() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    return upperCaseLetters.concat(lowerCaseLetters).concat(numbers);
  }

  /** Hàm chuẩn hóa tên file */
  public static String formatFileName(String fileName) {
    String temp = Normalizer.normalize(fileName, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    String name =
        pattern
            .matcher(temp)
            .replaceAll("")
            .toLowerCase()
            .replaceAll(" ", "_")
            .replaceAll("đ", "d");
    return name.toLowerCase() + "_" + System.currentTimeMillis();
  }
}
