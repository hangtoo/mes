package com.qcadoo.mes.orders.hooks;

import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.orders.constants.ScheduleFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;

@Service
public class ScheduleHooks {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void onSave(final DataDefinition scheduleDD, final Entity schedule) {
        setScheduleNumber(schedule);
    }

    private void setScheduleNumber(final Entity schedule) {
        if (checkIfShouldInsertNumber(schedule)) {
            String number = jdbcTemplate.queryForObject("select generate_schedule_number()", Collections.emptyMap(),
                    String.class);
            schedule.setField(ScheduleFields.NUMBER, number);
            if (StringUtils.isBlank(schedule.getStringField(ScheduleFields.NAME))) {
                schedule.setField(ScheduleFields.NAME, number);
            }
        }
    }

    private boolean checkIfShouldInsertNumber(final Entity schedule) {
        if (!Objects.isNull(schedule.getId())) {
            return false;
        }
        return !StringUtils.isNotBlank(schedule.getStringField(ScheduleFields.NUMBER));
    }
}
