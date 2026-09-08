package com.tanishqchcmd.timethread;

import java.util.Set;

public class PresenceUpdate {
    private Long groupId;
    private int onlineCount;
    private Set<Long> onlineStudentIds;

    public PresenceUpdate() {}

    public PresenceUpdate(Long groupId, int onlineCount, Set<Long> onlineStudentIds) {
        this.groupId = groupId;
        this.onlineCount = onlineCount;
        this.onlineStudentIds = onlineStudentIds;
    }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public int getOnlineCount() { return onlineCount; }
    public void setOnlineCount(int onlineCount) { this.onlineCount = onlineCount; }

    public Set<Long> getOnlineStudentIds() { return onlineStudentIds; }
    public void setOnlineStudentIds(Set<Long> onlineStudentIds) { this.onlineStudentIds = onlineStudentIds; }
}