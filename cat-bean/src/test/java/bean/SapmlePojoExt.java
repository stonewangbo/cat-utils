package bean;

public class SapmlePojoExt {


    private Long userId;

    private String lockId;

    private S1UseStatus status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public S1UseStatus getStatus() {
        return status;
    }

    public void setStatus(S1UseStatus status) {
        this.status = status;
    }
}