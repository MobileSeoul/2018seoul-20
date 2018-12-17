package buskinggo.seoul.com.buskinggo.utils;

import java.util.ArrayList;

import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;

public interface AsyncListener {
    void buskerComplete(BuskerDTO buskerDTO);

    void buskerComplete(ArrayList<BuskerDTO> buskerDTOS);

    void buskingComplete(ArrayList<BuskingDTO> buskingDTOS);

    void buskingComplete(BuskingDTO buskingDTOS);
}
