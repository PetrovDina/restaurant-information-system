package com.restaurant.backend.dto.reports;

import java.time.LocalDate;
import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WeeklyReportResultItemDTO extends AbstractDateReportResultItemDTO {
    private Integer weekNumber;
    private Year year;

    @Override
    public LocalDate getApproximateDate() {
        return LocalDate.ofYearDay(year.getValue(), weekNumber * 7 - 1);
    }

    @Override
    public String getLabel() {
        return year.toString().concat(" - Week ").concat(weekNumber.toString());
    }
}
