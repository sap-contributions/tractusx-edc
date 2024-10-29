/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/
package org.eclipse.tractusx.edc.controlplane.contract;

import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.validation.ContractValidationService;
import org.eclipse.edc.connector.controlplane.contract.validation.ContractValidationServiceImpl;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator.AtomicConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator.MultiplicityConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.PermissivePolicyEquality;

@Provides({ContractValidationService.class})
@Extension(value = ContractValidationExtension.NAME)
public class ContractValidationExtension implements ServiceExtension {
    public static final String NAME = "Contract Validation";

    @Inject
    private AssetIndex assetIndex;

    @Inject
    private PolicyEngine policyEngine;

    @Inject
    private TypeManager typeManager;

    @Inject
    private Monitor monitor;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {

        MultiplicityConstraintComparator multiplicityConstraintComparator = new MultiplicityConstraintComparator(typeManager);
        ConstraintComparator constraintComparator = new ConstraintComparator(multiplicityConstraintComparator, new AtomicConstraintComparator());
        var policyEqualityV2 = new PermissivePolicyEquality(typeManager, monitor, constraintComparator);
        var validationServiceV2 = new ContractValidationServiceImpl(assetIndex, policyEngine, policyEqualityV2);
        context.registerService(ContractValidationService.class, validationServiceV2);
    }
}
