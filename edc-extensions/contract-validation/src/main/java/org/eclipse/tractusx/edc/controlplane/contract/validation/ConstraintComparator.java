package org.eclipse.tractusx.edc.controlplane.contract.validation;

import org.eclipse.edc.policy.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


public class ConstraintComparator implements Comparator<Constraint> {


    @Override
    public int compare(Constraint c1, Constraint c2) {

        if (c1 instanceof MultiplicityConstraint mc1 && c2 instanceof MultiplicityConstraint mc2) {
            return Objects.compare(mc1, mc2, new MultiplicityConstraintComparator());
        }

        if (c1 instanceof AtomicConstraint ac1 && c2 instanceof AtomicConstraint ac2) {
            return Objects.compare(ac1, ac2, new AtomicConstraintComparator());
        }

        // one of them is MultiplicityConstraint and other is AtomicConstraint
        // sort MultiplicityConstraint < AtomicConstraint
        if (c1 instanceof MultiplicityConstraint) {
            return -1;
        } else {
            return 1;
        }
    }

    public static class AtomicConstraintComparator implements Expression.Visitor<Object>, Comparator<AtomicConstraint> {

        @Override
        public int compare(AtomicConstraint o1, AtomicConstraint o2) {


            return Comparator.comparing((AtomicConstraint o) -> expressionAsString(o.getLeftExpression()))
                    .thenComparing(AtomicConstraint::getOperator)
                    .thenComparing((AtomicConstraint o) -> expressionAsString(o.getRightExpression()))
                    .compare(o1, o2);
        }

        private String expressionAsString(Expression expression) {
            return String.valueOf(expression.accept(this));
        }

        @Override
        public Object visitLiteralExpression(LiteralExpression expression) {
            return expression.getValue();
        }
    }

    public static class MultiplicityConstraintComparator implements Comparator<MultiplicityConstraint> {

        @Override
        public int compare(MultiplicityConstraint o1, MultiplicityConstraint o2) {

            return Comparator.comparing((MultiplicityConstraint o) -> o.getClass().getName()).compare(o1, o2);
        }
    }
}

