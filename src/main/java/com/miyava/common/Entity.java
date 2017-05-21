package com.miyava.common;

import java.io.Serializable;

/**
 * general Entity interface which generifies over the ID type and provides a getId method.
 *
 * @param <ID>
 */
public interface Entity<ID extends Serializable> {

    ID getId();
}
