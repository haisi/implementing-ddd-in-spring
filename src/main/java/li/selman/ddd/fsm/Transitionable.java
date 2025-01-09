package li.selman.ddd.fsm;

import java.io.Serializable;

public interface Transitionable<T extends Serializable> {

    Transition<T> getTransition();

    T fromState(T current);
}
