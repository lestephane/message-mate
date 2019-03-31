package com.envimate.messageMate.soonToBeExternal.building;

import java.util.function.Function;

public interface UseCaseAdapterAddParameterValueExtractionBuilder<USECASE, EVENT> {

    <PARAM> UseCaseAdapterStep3Builder<USECASE, EVENT> mappingEventToParameter(Class<PARAM> paramClass, Function<EVENT, Object> mapping);
}
