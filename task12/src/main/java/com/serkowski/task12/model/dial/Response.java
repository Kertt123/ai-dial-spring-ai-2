package com.serkowski.task12.model.dial;


import com.serkowski.task12.model.dial.ResponseChoice;

import java.util.List;

public record Response(List<ResponseChoice> choices) {
}
