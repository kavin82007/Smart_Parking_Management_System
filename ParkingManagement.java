public class ParkingManagement {

    static class Slot {
        int slotNumber;
        String slotType;
        boolean occupied;
        String vehicleNumber;

        Slot(int slotNumber, String slotType) {
            this.slotNumber = slotNumber;
            this.slotType = slotType;
            this.occupied = false;
            this.vehicleNumber = "";
        }
    }

    static class Vehicle {
        String vehicleNumber;
        String vehicleType;
        boolean vip;
        boolean electric;

        Vehicle(String vehicleNumber, String vehicleType,
                boolean vip, boolean electric) {

            this.vehicleNumber = vehicleNumber;
            this.vehicleType = vehicleType;
            this.vip = vip;
            this.electric = electric;
        }
    }

    static class ParkingTicket {
        String ticketId;
        String vehicleNumber;
        int slotNumber;
        long entryTime;
        boolean active;

        ParkingTicket(String ticketId, String vehicleNumber,
                      int slotNumber, long entryTime) {

            this.ticketId = ticketId;
            this.vehicleNumber = vehicleNumber;
            this.slotNumber = slotNumber;
            this.entryTime = entryTime;
            this.active = true;
        }
    }

    static Slot[] slots = {
        new Slot(1, "Bike"),
        new Slot(2, "Bike"),

        new Slot(3, "Car"),
        new Slot(4, "Car"),

        new Slot(5, "SUV"),
        new Slot(6, "SUV"),

        new Slot(7, "Truck"),

        new Slot(8, "EV"),
        new Slot(9, "EV"),

        new Slot(10, "VIP")
    };

    // Check whether vehicle type is valid
    static boolean validVehicleType(String type) {

        return type.equalsIgnoreCase("Bike")
                || type.equalsIgnoreCase("Car")
                || type.equalsIgnoreCase("SUV")
                || type.equalsIgnoreCase("Truck")
                || type.equalsIgnoreCase("Electric");
    }

    // Automatically select an appropriate slot
    public static Slot findSlot(Vehicle vehicle) {

        if (!validVehicleType(vehicle.vehicleType)) {
            throw new IllegalArgumentException(
                    "Invalid vehicle type");
        }

        // VIP vehicles get VIP slot
        if (vehicle.vip) {

            for (Slot slot : slots) {

                if (slot.slotType.equals("VIP")
                        && !slot.occupied) {

                    return slot;
                }
            }

            throw new IllegalStateException(
                    "VIP parking full");
        }

        // EV gets EV charging slot
        if (vehicle.electric) {

            for (Slot slot : slots) {

                if (slot.slotType.equals("EV")
                        && !slot.occupied) {

                    return slot;
                }
            }

            throw new IllegalStateException(
                    "EV parking full");
        }

        // Normal vehicle gets matching slot
        for (Slot slot : slots) {

            if (!slot.occupied
                    && slot.slotType.equalsIgnoreCase(
                            vehicle.vehicleType)) {

                return slot;
            }
        }

        throw new IllegalStateException(
                "Parking lot full for vehicle type");
    }

    // Vehicle entry
    public static ParkingTicket enterVehicle(
            Vehicle vehicle,
            String ticketId) {

        if (vehicle == null) {
            throw new IllegalArgumentException(
                    "Invalid vehicle");
        }

        if (vehicle.vehicleNumber == null
                || vehicle.vehicleNumber.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Invalid vehicle number");
        }

        // Prevent duplicate vehicle
        for (Slot slot : slots) {

            if (slot.occupied
                    && slot.vehicleNumber.equalsIgnoreCase(
                            vehicle.vehicleNumber)) {

                throw new IllegalStateException(
                        "Vehicle already parked");
            }
        }

        Slot slot = findSlot(vehicle);

        slot.occupied = true;
        slot.vehicleNumber = vehicle.vehicleNumber;

        return new ParkingTicket(
                ticketId,
                vehicle.vehicleNumber,
                slot.slotNumber,
                System.currentTimeMillis());
    }

    // Vehicle exit
    public static double exitVehicle(
            ParkingTicket ticket,
            int parkingHours,
            boolean peakHour) {

        if (ticket == null || !ticket.active) {
            throw new IllegalArgumentException(
                    "Invalid or inactive ticket");
        }

        if (parkingHours < 0) {
            throw new IllegalArgumentException(
                    "Invalid parking duration");
        }

        Slot parkedSlot = null;

        for (Slot slot : slots) {

            if (slot.slotNumber == ticket.slotNumber
                    && slot.occupied
                    && slot.vehicleNumber.equalsIgnoreCase(
                            ticket.vehicleNumber)) {

                parkedSlot = slot;
                break;
            }
        }

        if (parkedSlot == null) {
            throw new IllegalStateException(
                    "Vehicle not found");
        }

        double fee =
                calculateParkingFee(
                        parkedSlot,
                        parkingHours,
                        peakHour);

        parkedSlot.occupied = false;
        parkedSlot.vehicleNumber = "";

        ticket.active = false;

        return fee;
    }

    // Parking fee calculation
    public static double calculateParkingFee(
            Slot slot,
            int hours,
            boolean peakHour) {

        if (hours < 0) {
            throw new IllegalArgumentException(
                    "Invalid parking hours");
        }

        double hourlyRate;

        if (slot.slotType.equals("Bike")) {
            hourlyRate = 20;
        }
        else if (slot.slotType.equals("Car")) {
            hourlyRate = 50;
        }
        else if (slot.slotType.equals("SUV")) {
            hourlyRate = 70;
        }
        else if (slot.slotType.equals("Truck")) {
            hourlyRate = 100;
        }
        else if (slot.slotType.equals("EV")) {
            hourlyRate = 60;
        }
        else if (slot.slotType.equals("VIP")) {
            hourlyRate = 100;
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid slot type");
        }

        double fee = hourlyRate * hours;

        // Minimum parking charge
        if (hours > 0 && fee < hourlyRate) {
            fee = hourlyRate;
        }

        // Peak-hour surcharge
        if (peakHour) {
            fee *= 1.25;
        }

        return fee;
    }

    // Lost ticket handling
    public static double lostTicketCharge(
            String vehicleType) {

        if (!validVehicleType(vehicleType)) {
            throw new IllegalArgumentException(
                    "Invalid vehicle type");
        }

        // Fixed lost-ticket penalty
        return 500;
    }

    // EV charging fee
    public static double calculateEVChargingFee(
            double unitsConsumed) {

        if (unitsConsumed < 0) {
            throw new IllegalArgumentException(
                    "Invalid charging units");
        }

        return unitsConsumed * 15;
    }

    public static void main(String[] args) {

        // Built-in input
        Vehicle vehicle = new Vehicle(
                "TN01AB1234",
                "Car",
                false,
                false);

        try {

            System.out.println(
                    "======= SMART PARKING SYSTEM =======");

            ParkingTicket ticket =
                    enterVehicle(
                            vehicle,
                            "T001");

            System.out.println(
                    "Vehicle Number : "
                            + vehicle.vehicleNumber);

            System.out.println(
                    "Vehicle Type   : "
                            + vehicle.vehicleType);

            System.out.println(
                    "Slot Number    : "
                            + ticket.slotNumber);

            System.out.println(
                    "Ticket ID      : "
                            + ticket.ticketId);

            // Example: vehicle parks for 3 hours
            double fee =
                    exitVehicle(
                            ticket,
                            3,
                            false);

            System.out.printf(
                    "Parking Fee    : Rs.%.2f\n",
                    fee);

            System.out.println(
                    "Vehicle exited successfully.");

            System.out.println(
                    "====================================");

        } catch (Exception e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}