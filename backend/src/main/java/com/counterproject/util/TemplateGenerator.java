package com.counterproject.util;

import com.counterproject.entity.Diff;
import com.counterproject.entity.FrequencyUnit;
import com.counterproject.entity.Queue;
import com.counterproject.entity.Template;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TemplateGenerator {

    public static List<Diff> generateDiffs(Queue queue, Template template, LocalDate dtStartLink, LocalDate dtEndLink) {
        List<Diff> diffs = new ArrayList<>();

        // Calculate first occurrence date
        LocalDate firstDate = calculateFirstOccurrence(template, dtStartLink);

        LocalDate currentDate = firstDate;
        while (!currentDate.isAfter(dtEndLink)) {
            Diff diff = new Diff();
            diff.setQueue(queue);
            diff.setValue(template.getValue());
            diff.setDtDiff(currentDate.atStartOfDay());
            diff.setDtExpiry(calculateExpiry(currentDate, template));
            diff.setIsManual(false);
            diff.setDescription("Generated from template: " + template.getName());
            diff.setTemplateId(template.getId());
            diff.setModifiedManually(false);

            diffs.add(diff);

            currentDate = calculateNextDate(currentDate, template);
        }

        return diffs;
    }

    private static LocalDate calculateFirstOccurrence(Template template, LocalDate dtStartLink) {
        switch (template.getFrequencyUnit()) {
            case DAYS:
                return dtStartLink;
            case WEEKS:
                LocalDate weekStart = dtStartLink;
                while (weekStart.getDayOfWeek().getValue() != template.getDayOfPeriod()) {
                    weekStart = weekStart.plusDays(1);
                }
                return weekStart;
            case MONTHS:
                int targetDay = template.getDayOfPeriod();
                YearMonth yearMonth = YearMonth.from(dtStartLink);
                int actualDay = Math.min(targetDay, yearMonth.lengthOfMonth());
                LocalDate monthDate = yearMonth.atDay(actualDay);
                if (monthDate.isBefore(dtStartLink)) {
                    yearMonth = yearMonth.plusMonths(1);
                    actualDay = Math.min(targetDay, yearMonth.lengthOfMonth());
                    monthDate = yearMonth.atDay(actualDay);
                }
                return monthDate;
            case YEARS:
                int targetDayOfYear = template.getDayOfPeriod();
                int year = dtStartLink.getYear();
                LocalDate yearDate = LocalDate.ofYearDay(year, Math.min(targetDayOfYear, Year.of(year).length()));
                if (yearDate.isBefore(dtStartLink)) {
                    year++;
                    yearDate = LocalDate.ofYearDay(year, Math.min(targetDayOfYear, Year.of(year).length()));
                }
                return yearDate;
            default:
                throw new IllegalArgumentException("Unknown frequency unit");
        }
    }

    private static LocalDate calculateNextDate(LocalDate currentDate, Template template) {
        switch (template.getFrequencyUnit()) {
            case DAYS:
                return currentDate.plusDays(template.getFrequencyNum());
            case WEEKS:
                return currentDate.plusWeeks(template.getFrequencyNum());
            case MONTHS:
                int targetDay = template.getDayOfPeriod();
                YearMonth nextMonth = YearMonth.from(currentDate).plusMonths(template.getFrequencyNum());
                int actualDay = Math.min(targetDay, nextMonth.lengthOfMonth());
                return nextMonth.atDay(actualDay);
            case YEARS:
                int targetDayOfYear = template.getDayOfPeriod();
                int nextYear = currentDate.getYear() + template.getFrequencyNum();
                return LocalDate.ofYearDay(nextYear, Math.min(targetDayOfYear, Year.of(nextYear).length()));
            default:
                throw new IllegalArgumentException("Unknown frequency unit");
        }
    }

    private static LocalDate calculateExpiry(LocalDate diffDate, Template template) {
        switch (template.getFrequencyUnit()) {
            case DAYS:
                return diffDate.plusDays(template.getFrequencyNum());
            case WEEKS:
                return diffDate.plusWeeks(template.getFrequencyNum());
            case MONTHS:
                return diffDate.plusMonths(template.getFrequencyNum());
            case YEARS:
                return diffDate.plusYears(template.getFrequencyNum());
            default:
                throw new IllegalArgumentException("Unknown frequency unit");
        }
    }
}
