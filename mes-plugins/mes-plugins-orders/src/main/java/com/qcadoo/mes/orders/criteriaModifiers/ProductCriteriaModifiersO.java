package com.qcadoo.mes.orders.criteriaModifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.orders.constants.OrdersConstants;
import com.qcadoo.mes.orders.constants.SalesPlanProductFields;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.search.*;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;

@Service
public class ProductCriteriaModifiersO {

    public static final String L_SALES_PLAN_ID = "salesPlanId";

    public static final String L_PRODUCT_ID = "productId";

    private static final String L_THIS_ID = "this.id";

    private static final String L_DOT = ".";

    private static final String L_ID = "id";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void showNotAssignedProducts(final SearchCriteriaBuilder searchCriteriaBuilder, final FilterValueHolder filterValue) {
        if (filterValue.has(L_SALES_PLAN_ID)) {
            long salesPlanId = filterValue.getLong(L_SALES_PLAN_ID);

            SearchCriteriaBuilder subCriteria = dataDefinitionService
                    .get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_SALES_PLAN_PRODUCT)
                    .findWithAlias(OrdersConstants.MODEL_SALES_PLAN_PRODUCT)
                    .createAlias(SalesPlanProductFields.PRODUCT, SalesPlanProductFields.PRODUCT, JoinType.INNER)
                    .add(SearchRestrictions.eqField(SalesPlanProductFields.PRODUCT + L_DOT + L_ID, L_THIS_ID))
                    .add(SearchRestrictions.belongsTo(SalesPlanProductFields.SALES_PLAN, OrdersConstants.PLUGIN_IDENTIFIER,
                            OrdersConstants.MODEL_SALES_PLAN, salesPlanId))
                    .setProjection(SearchProjections.id());
            if (filterValue.has(L_PRODUCT_ID)) {
                searchCriteriaBuilder.add(SearchRestrictions.or(SearchSubqueries.notExists(subCriteria),
                        SearchRestrictions.idEq(filterValue.getLong(L_PRODUCT_ID))));
            } else {
                searchCriteriaBuilder.add(SearchSubqueries.notExists(subCriteria));
            }
        }

    }
}
