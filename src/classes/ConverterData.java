/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author wynvern
 */
public class ConverterData {
    public static Timestamp converterEmData(String dateString) {
        try {
            // Parse the input date string
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yy");
            Date parsedDate = inputDateFormat.parse(dateString);

            // Set the time to 12:00:00
            parsedDate.setHours(12);
            parsedDate.setMinutes(0);
            parsedDate.setSeconds(0);

            // Convert Date to Timestamp
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
}
