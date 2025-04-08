package com.bmisiek.printer.contract;

public interface PrinterInterface<T> {
    public boolean Supports(Class<?> className);

    public void Print(T object);
}
