package org.reyzis.mapper;

public interface Mapper <F,T> {

    T mapFrom(F object);
}
