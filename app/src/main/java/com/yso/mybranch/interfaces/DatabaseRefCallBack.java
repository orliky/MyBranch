package com.yso.mybranch.interfaces;

import java.util.List;

/**
 * Created by Admin on 06-Nov-17.
 */

public interface DatabaseRefCallBack<T>
{
    void onGetBranches(List<T> branches);
}


