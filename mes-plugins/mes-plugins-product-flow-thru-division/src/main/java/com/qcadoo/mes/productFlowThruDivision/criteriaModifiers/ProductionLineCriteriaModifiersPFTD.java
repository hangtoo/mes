/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.productFlowThruDivision.criteriaModifiers;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.technologies.constants.TechnologyOperationComponentFields;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;

@Service
public class ProductionLineCriteriaModifiersPFTD {

    public static final String DIVISION_PARAMETER = "divisionID";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void showLinesForDivision(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long divisionId = null;
        if (!filterValue.has(DIVISION_PARAMETER)) {
            return;
        } else {
            divisionId = filterValue.getLong(DIVISION_PARAMETER);
        }
        Entity division = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_DIVISION)
                .get(divisionId);
        Collection<Long> productionLinesIds = CollectionUtils
                .collect(division.getHasManyField("productionLines"),
                        new Transformer() {

                            public Long transform(Object o) {
                                return ((Entity) o).getId();
                            }
                        });

        if (productionLinesIds.isEmpty()) {
            return;
        }
        scb.add(SearchRestrictions
                .in("id", productionLinesIds));
    }

    public void showWorkstationsForProductionLine(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(TechnologyOperationComponentFields.PRODUCTION_LINE)) {
            Long productionLineId = filterValue.getLong(TechnologyOperationComponentFields.PRODUCTION_LINE);
            scb.createAlias(TechnologyOperationComponentFields.PRODUCTION_LINE,
                    TechnologyOperationComponentFields.PRODUCTION_LINE, JoinType.INNER).add(
                    SearchRestrictions.eq(TechnologyOperationComponentFields.PRODUCTION_LINE + ".id", productionLineId));
        }
    }
}
