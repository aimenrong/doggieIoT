package com.at.doggie.ct.exception;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public class MongoDaoException extends Exception {
    public MongoDaoException(Exception e) {
        super(e);
    }

    public MongoDaoException(String str) {
        super(str);
    }
}
