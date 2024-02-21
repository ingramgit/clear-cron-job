package com.myspace;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class TimerCleaner implements java.io.Serializable {

    static final long serialVersionUID = 1L;
    static final String ETS_JNDI_NAME = "java:module/EJBTimerScheduler";
    static final String JOB_HANDLE_CLASS_NAME = "org.drools.core.time.JobHandle";

    public TimerCleaner() {
    }

    public static void removeOrphanedTimers(ArrayList<String> uuids){
        try {
            final Hashtable jndiProperties = new Hashtable();
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            final Context context = new InitialContext(jndiProperties);
            
            Object ejbTimerScheduler = context.lookup(ETS_JNDI_NAME);
            Method getTimerByNameMethod = null;
            Object globalJpaTimerJobInstance = null;
            Method getJobHandleMethod = null;
            Object jobHandle = null;
            Method removeJobMethod = null;
            
            final Class jobHandleClass = Class.forName(JOB_HANDLE_CLASS_NAME);

            if (null != ejbTimerScheduler){
                getTimerByNameMethod = ejbTimerScheduler.getClass().getMethod("getTimerByName", String.class);
                
                for(String uuid: uuids){
                    globalJpaTimerJobInstance = getTimerByNameMethod.invoke(ejbTimerScheduler, uuid);
                    System.out.println("Job info for " + uuid + " is: " + globalJpaTimerJobInstance);
                    
                    if(null != globalJpaTimerJobInstance){
                        getJobHandleMethod = globalJpaTimerJobInstance.getClass().getMethod("getJobHandle");
                        jobHandle = getJobHandleMethod.invoke(globalJpaTimerJobInstance);
    
                        removeJobMethod = ejbTimerScheduler.getClass().getMethod("removeJob", jobHandleClass);
                        System.out.println("EJB timer removal status for " + uuid + ": " + removeJobMethod.invoke(ejbTimerScheduler, jobHandle));
                    }
                }
            }
            else{
                System.out.println("EjbTimerScheduler instance not found: " + ejbTimerScheduler);
            }
        } catch (NamingException |  NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}