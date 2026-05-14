package custom_package;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class sjfscheduler {
    public static void main(String[] args) {
        Log.printLine("Starting Custom SJF Scheduler...");
        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            Datacenter datacenter = createDatacenter("Datacenter_0");
            DatacenterBroker broker = new DatacenterBroker("Broker");
            int brokerId = broker.getId();

            // Create VM with Space-Shared scheduler to see the effect of sorting
            Vm vm = new Vm(0, brokerId, 1000, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerSpaceShared());
            List<Vm> vmList = Collections.singletonList(vm);
            broker.submitVmList(vmList);

            // 1. CREATE MULTIPLE CLOUDLETS
            List<Cloudlet> cloudletList = new ArrayList<>();
            UtilizationModel util = new UtilizationModelFull();
            
            // Task 0 is LONG, Task 1 is SHORT
            cloudletList.add(new Cloudlet(0, 80000, 1, 300, 300, util, util, util)); 
            cloudletList.add(new Cloudlet(1, 20000, 1, 300, 300, util, util, util)); 
            
            for(Cloudlet c : cloudletList) c.setUserId(brokerId);

            // 2. APPLY SJF ALGORITHM (The "Custom" part)
            Collections.sort(cloudletList, (c1, c2) -> Long.compare(c1.getCloudletLength(), c2.getCloudletLength()));
            Log.printLine("Algorithm Applied: Shortest Job First (SJF)");

            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<Cloudlet> finishedCloudlets = broker.getCloudletReceivedList();
            printCloudletList(finishedCloudlets);

        } catch (Exception e) {
            Log.printLine("Simulation error: " + e.getMessage());
        }
    }

    
    private static Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));
        
        hostList.add(new Host(0, new RamProvisionerSimple(2048), new BwProvisionerSimple(10000), 1000000, peList, new VmSchedulerTimeShared(peList)));
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics("x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);
        
        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
    }

    private static void printCloudletList(List<Cloudlet> list) {
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID\tSTATUS\tData center ID\tVM ID\tTime\tStart Time\tFinish Time");
        DecimalFormat dft = new DecimalFormat("###.##");
        for (Cloudlet cloudlet : list) {
            Log.printLine(cloudlet.getCloudletId() + "\t\t" + Cloudlet.getStatusString(cloudlet.getCloudletStatus()) +
                    "\t\t" + cloudlet.getResourceId() + "\t\t" + cloudlet.getVmId() + "\t\t" +
                    dft.format(cloudlet.getActualCPUTime()) + "\t\t" + dft.format(cloudlet.getExecStartTime()) +
                    "\t\t" + dft.format(cloudlet.getFinishTime()));
        }
    }
}
