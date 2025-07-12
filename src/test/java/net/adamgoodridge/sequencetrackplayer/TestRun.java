package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
public class TestRun {
    private static final Random rnd = new Random();
    public static void main(String[] args) {
        System.out.println(rnd.nextInt(1000));
        System.out.println(rnd.nextInt(1000));
        System.out.println(rnd.nextInt(1000));
        System.out.println(rnd.nextInt(1000));
        System.out.println(rnd.nextInt(1000));
        System.out.println(rnd.nextInt(1000));
    }
}

