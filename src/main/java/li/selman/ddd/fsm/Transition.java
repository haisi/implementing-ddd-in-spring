package li.selman.ddd.fsm;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class Transition<T extends Serializable> implements Serializable {

    private T currentState;
    private T finalState;
    private Collection<T> validFromStates;
    private Collection<T> ignoreFromStates = Set.of();

    public static <T extends Serializable> InitializedTransitionBuilder<T> initialState(T initialState) {
        return new InitializedTransitionBuilder<>(initialState);
    }

    public static class InitializedTransitionBuilder<T extends Serializable> {
        private final T initialState;

        public InitializedTransitionBuilder(T initialState) {
            this.initialState = initialState;
        }

        @SafeVarargs
        public final TransitionBuilder<T> from(T... validFromStates) {
            var transitionBuilder = Transition.from(validFromStates);
            transitionBuilder.transition.current(initialState);
            return transitionBuilder;
        }
    }

    @SafeVarargs
    public static <T extends Serializable> TransitionBuilder<T> from(T... validFromStates) {
        return new TransitionBuilder<>(validFromStates);
    }

    public static class TransitionBuilder<T extends Serializable> {
        private final Transition<T> transition = new Transition<>();

        @SafeVarargs
        protected TransitionBuilder(T... validFromStates) {
            transition.validFromStates = Set.of(validFromStates);
        }

        @SafeVarargs
        public final TransitionBuilder<T> ignoreFromStates(T... ignoreFromStates) {
            transition.ignoreFromStates = Set.of(ignoreFromStates);
            return this;
        }

        public Transition<T> to(T state) {
            transition.finalState = state;
            return transition;
        }
    }

    public Transition<T> current(T state) {
        this.currentState = state;
        return this;
    }

    public boolean isValid() {
        return validFromStates.contains(currentState);
    }

    public T getFinalState() {
        if (finalState == null) {
            return currentState;
        }
        return finalState;
    }

    public <E extends Exception> T evaluateTransition(T fromState, Supplier<E> errorToThrow) throws E {
        if (this.current(fromState).isValid()) {
            return this.getFinalState();
        }
        if (this.ignoreFromStates.contains(fromState)) {
            return fromState;
        }
        throw errorToThrow.get();
    }
}
