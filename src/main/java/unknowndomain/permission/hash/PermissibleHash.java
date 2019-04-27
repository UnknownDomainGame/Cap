package unknowndomain.permission.hash;

import unknowndomain.permission.Permissible;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PermissibleHash implements Permissible {

    private HashMap<String, Boolean> permissionMap = new HashMap<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean hasPermission(String permission) {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            if (permission == null || permission.isEmpty())
                return false;
            readLock.lock();
            if (permissionMap.containsKey(permission))
                return permissionMap.get(permission);
            while(true){
                int lastDot = permission.lastIndexOf('.');
                if(lastDot<=0)
                    break;
                permission = permission.substring(0,lastDot);
                if (permissionMap.containsKey(permission))
                    return permissionMap.get(permission);
            }
        } finally {
            readLock.unlock();
        }
        return false;
    }

    @Override
    public void definePermission(String permission, boolean bool) {
        if(permission==null)
            return;
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        permissionMap.put(permission, bool);
        writeLock.unlock();
    }

    public void undefinePermission(String permission) {
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        permissionMap.remove(permission);
        writeLock.unlock();
    }

    public Map<String,Boolean> getPermissionMap(){
        return Collections.unmodifiableMap(permissionMap);
    }

}
