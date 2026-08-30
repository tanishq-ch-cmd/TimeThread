package com.tanishqchcmd.timethread;

import java.time.LocalDateTime;

public record ChatMessageResponse(Long id, String content, String senderName, Long senderId, LocalDateTime timestamp) {}