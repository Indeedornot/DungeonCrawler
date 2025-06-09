package com.bmisiek.printer.contract;

public interface PrinterInterface<T> {
    boolean Supports(Class<?> className);

    void Print(T object);
}
