package com.counterproject.service;

import com.counterproject.dto.MetricsDTO;
import com.counterproject.entity.Diff;
import com.counterproject.entity.Queue;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MetricsService {

    @PersistenceContext(unitName = "counterproject-pu")
    private EntityManager em;

    public MetricsDTO calculateMetrics(Long queueId, LocalDate dtRif) {
        if (dtRif == null) {
            dtRif = LocalDate.now();
        }

        Queue queue = em.find(Queue.class, queueId);
        if (queue == null) {
            throw new IllegalArgumentException("Queue not found");
        }

        MetricsDTO metrics = new MetricsDTO();
        metrics.setDtRif(dtRif);

        // Build myDiffList: init + all diffs <= dtRif
        List<DiffData> myDiffList = buildDiffList(queue, dtRif);

        // CURRENTVALUE
        BigDecimal currentValue = calculateCurrentValue(myDiffList);
        metrics.setCurrentValue(currentValue);

        // SPESEDTRIF + SPESEEXPECTEDDTRIF
        LocalDateTime dtRifSOF = dtRif.atStartOfDay();
        LocalDateTime dtRifEOD = dtRif.atTime(23, 59, 59);

        BigDecimal speseAllaDtRifEOD = BigDecimal.ZERO;
        BigDecimal spesaExpectedAllaDtRifEOD = BigDecimal.ZERO;

        for (DiffData diff : myDiffList) {
            if (diff.value.compareTo(BigDecimal.ZERO) < 0) {
                if (!diff.dtDiff.isBefore(dtRifSOF) && !diff.dtDiff.isAfter(dtRifEOD)) {
                    speseAllaDtRifEOD = speseAllaDtRifEOD.add(diff.value);
                    if (Boolean.FALSE.equals(diff.isManual)) {
                        spesaExpectedAllaDtRifEOD = spesaExpectedAllaDtRifEOD.add(diff.value);
                    }
                }
            }
        }

        metrics.setSpeseAllaDtRifEOD(speseAllaDtRifEOD);
        metrics.setSpesaExpectedAllaDtRifEOD(spesaExpectedAllaDtRifEOD);

        // EXPECTEDVALUE
        BigDecimal speseManualiAllaDtRifEOD = speseAllaDtRifEOD.subtract(spesaExpectedAllaDtRifEOD);
        BigDecimal expectedValue = currentValue.subtract(speseManualiAllaDtRifEOD);

        // LINEARCONSUMPTION
        if (queue.isWithLinearConsumption()) {
            for (DiffData diff : myDiffList) {
                if (diff.value.compareTo(BigDecimal.ZERO) > 0 && diff.dtExpiry != null) {
                    long daysBetween = ChronoUnit.DAYS.between(diff.dtDiff.toLocalDate(), diff.dtExpiry);
                    if (daysBetween > 0) {
                        BigDecimal dailyConsumption = diff.value.divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP);
                        spesaExpectedAllaDtRifEOD = spesaExpectedAllaDtRifEOD.subtract(dailyConsumption);

                        LocalDate minDate = dtRif.isBefore(diff.dtExpiry) ? dtRif : diff.dtExpiry;
                        long daysConsumed = ChronoUnit.DAYS.between(diff.dtDiff.toLocalDate(), minDate);
                        BigDecimal totalConsumed = dailyConsumption.multiply(BigDecimal.valueOf(daysConsumed));
                        expectedValue = expectedValue.subtract(totalConsumed);
                    }
                }
            }
        }

        // BONUS
        List<DiffData> bonusList = new ArrayList<>();
        for (DiffData diff : myDiffList) {
            if (diff.value.compareTo(BigDecimal.ZERO) > 0 && diff.dtExpiry == null) {
                bonusList.add(diff);
            }
        }

        if (!bonusList.isEmpty()) {
            BigDecimal totBonus = BigDecimal.ZERO;
            for (DiffData bonus : bonusList) {
                totBonus = totBonus.add(bonus.value);
            }

            BigDecimal bonusPerDayRemaining = totBonus;
            LocalDate dtMax = findMaxExpiry(myDiffList);

            if (dtMax != null && !dtRif.isAfter(dtMax)) {
                long daysRemaining = ChronoUnit.DAYS.between(dtRif, dtMax);
                if (daysRemaining > 0) {
                    bonusPerDayRemaining = totBonus.divide(BigDecimal.valueOf(daysRemaining), 2, RoundingMode.HALF_UP);
                }
            }

            spesaExpectedAllaDtRifEOD = spesaExpectedAllaDtRifEOD.subtract(bonusPerDayRemaining);
            expectedValue = expectedValue.subtract(bonusPerDayRemaining);
        }

        metrics.setSpesaExpectedAllaDtRifEOD(spesaExpectedAllaDtRifEOD);
        metrics.setExpectedValue(expectedValue);

        // BUDGETCURRENT
        BigDecimal budgetCurrent;
        if (currentValue.compareTo(BigDecimal.ZERO) <= 0) {
            budgetCurrent = BigDecimal.ZERO;
        } else {
            budgetCurrent = currentValue;
        }

        LocalDate dtMax = findMaxExpiry(myDiffList);
        metrics.setDtMaxExpiry(dtMax);
        metrics.setHasDtMaxExpiry(dtMax != null);

        if (dtMax != null && !dtRif.isAfter(dtMax)) {
            long daysRemaining = ChronoUnit.DAYS.between(dtRif, dtMax);
            if (daysRemaining > 0) {
                budgetCurrent = currentValue.divide(BigDecimal.valueOf(daysRemaining), 2, RoundingMode.HALF_UP);
            }
        }

        metrics.setBudgetCurrent(budgetCurrent);

        return metrics;
    }

    private List<DiffData> buildDiffList(Queue queue, LocalDate dtRif) {
        List<DiffData> list = new ArrayList<>();

        // Add init as diff
        DiffData initDiff = new DiffData();
        initDiff.value = queue.getInitValue();
        initDiff.dtDiff = queue.getDtInitValue();
        initDiff.dtExpiry = queue.getDtExpiryInitValue();
        initDiff.isManual = queue.getInitValue().compareTo(BigDecimal.ZERO) < 0 ? Boolean.TRUE : null;
        list.add(initDiff);

        // Add all diffs <= dtRif
        LocalDateTime dtRifEOD = dtRif.atTime(23, 59, 59);
        for (Diff diff : queue.getDiffs()) {
            if (!diff.getDtDiff().isAfter(dtRifEOD)) {
                DiffData data = new DiffData();
                data.value = diff.getValue();
                data.dtDiff = diff.getDtDiff();
                data.dtExpiry = diff.getDtExpiry();
                data.isManual = diff.getIsManual();
                list.add(data);
            }
        }

        return list;
    }

    private BigDecimal calculateCurrentValue(List<DiffData> myDiffList) {
        BigDecimal currentValue = BigDecimal.ZERO;
        for (DiffData diff : myDiffList) {
            currentValue = currentValue.add(diff.value);
        }
        return currentValue;
    }

    private LocalDate findMaxExpiry(List<DiffData> myDiffList) {
        LocalDate max = null;
        for (DiffData diff : myDiffList) {
            if (diff.value.compareTo(BigDecimal.ZERO) > 0 && diff.dtExpiry != null) {
                if (max == null || diff.dtExpiry.isAfter(max)) {
                    max = diff.dtExpiry;
                }
            }
        }
        return max;
    }

    private static class DiffData {
        BigDecimal value;
        LocalDateTime dtDiff;
        LocalDate dtExpiry;
        Boolean isManual;
    }
}
