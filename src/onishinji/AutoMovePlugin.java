package onishinji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import onishinji.commands.AutoMoveCommand;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class AutoMovePlugin extends JavaPlugin {

    private Timer t;

    HashMap<Player, Timer> timerPlayer;

    HashMap<Player, ArrayList<Location>> registerUser;

    HashMap<Player, AutoMoveInfo> playerAutoMoved;

    @Override
    public void onDisable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEnable() {
        // TODO Auto-generated method stub
        System.out.println(">>> AutoMove is On");

        this.registerUser = new HashMap<Player, ArrayList<Location>>();
        this.timerPlayer = new HashMap<Player, Timer>();
        this.playerAutoMoved = new HashMap<Player, AutoMoveInfo>();

        getCommand("am").setExecutor(new AutoMoveCommand(this, true));
        t = new Timer();

    }

    public void startTP(final Player player, String[] args) {
        // TODO Auto-generated method stub

        Location oldLocation = player.getLocation();
        final Location newLocation = oldLocation;

        final int i = 0;

        t.schedule(new TimerTask() {

            public void run() {

                // mouvement sur X
                newLocation.setX(newLocation.getX() + 0.1);

                // mouvement de tete vers la droite
                // valeur possible:
                // -90(sud)
                // -180 (est)
                // 180 (ouest)
                // 90(orienté nord)
                newLocation.setYaw((float) (newLocation.getYaw() + 0.1));

                // mouvement de tete vers le bas, valeur compris entre -90 (vers
                // le haut du ciel)
                // et +90 tu mattes tes pieds
                newLocation.setPitch((float) (newLocation.getPitch() + 0.1));
                player.teleport(newLocation);

            }

        }, 0, 1000);

    }

    public void stopTP(Player player, String[] args) {
        t.cancel();
        t = new Timer();
    }

    public ArrayList<Location> getLocations(Player player) {
        if (!(this.registerUser.get(player) != null)) {
            this.registerUser.put(player, new ArrayList<Location>());
        }

        return this.registerUser.get(player);
    }

    public Timer getTimer(Player player, boolean b) {
        if (!(this.timerPlayer.get(player) != null)) {
            this.timerPlayer.put(player, new Timer());
        }

        Timer t = this.timerPlayer.get(player);

        if (b) {
            t = new Timer();
            this.timerPlayer.put(player, t);
        }

        return this.timerPlayer.get(player);
    }

    public AutoMoveInfo getPlayerAutoMoved(Player player) {
        if (!(this.playerAutoMoved.get(player) != null)) {
            this.playerAutoMoved.put(player, new AutoMoveInfo());
        }

        return this.playerAutoMoved.get(player);
    }

    public void updatePlayerAutoMoved(Player player, AutoMoveInfo info) {
        this.playerAutoMoved.put(player, info);
    }

    public boolean playerCanBeAnimated(Player player) {
        return this.getLocations(player).size() == 2;
    }

    public void registerFirstCoordinate(Player player) {
        this.getLocations(player).add(0, player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Tu viens de définir le premier point du déplacement");
    }

    public void registerSecondCoordinate(Player player) {
        this.getLocations(player).add(1, player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Tu viens de définir le second point du déplacement");
    }

    public void startLineAnimation(final Player player, String intervalTp, String dureTotale) {
        // TODO Auto-generated method stub

        if (!this.playerCanBeAnimated(player)) {
            player.sendMessage("Commence par un /am start  et fini par un /am stop");
        }

        final long intervalDeTempsEntreLesTp = Long.parseLong(intervalTp);
        final long LadurédelavidéoEnMinute = Long.parseLong(dureTotale);

        // nombre d'étape
        final long nbEtapes = LadurédelavidéoEnMinute * 60 / intervalDeTempsEntreLesTp;

        AutoMoveInfo info = this.getPlayerAutoMoved(player);
        info.setNbTotalSteps((int) nbEtapes);
        this.updatePlayerAutoMoved(player, info);

        // Téléporte le player à la 1er coordonée
        ArrayList<Location> positions = this.getLocations(player);
        player.teleport(positions.get(0));

        // Les positions
        Location initPos = positions.get(0);
        Location finalPos = positions.get(1);

        // calcule du decalage en X
        final double offsetX = (finalPos.getX() - initPos.getX()) / nbEtapes;
        final double offsetY = (finalPos.getY() - initPos.getY()) / nbEtapes;
        final double offsetZ = (finalPos.getZ() - initPos.getZ()) / nbEtapes;

        final double offetYaw = ((finalPos.getYaw()) - initPos.getYaw()) / nbEtapes;
        final double offsetPitch = (finalPos.getPitch() - initPos.getPitch()) / nbEtapes;

        // Variable temporaire
        Location oldLocation = player.getLocation();
        final Location newLocation = oldLocation;

        final Player player2 = player;

        t = this.getTimer(player, true);

        t.schedule(new TimerTask() {

            long nbCurrentEtape = nbEtapes;

            public void run() {

                nbCurrentEtape--;

                AutoMoveInfo info = getPlayerAutoMoved(player);
                info.setCurrentStep((int) (info.getNbTotalSteps() - nbCurrentEtape));
                updatePlayerAutoMoved(player, info);

                if (nbCurrentEtape <= 0) {
                    info = new AutoMoveInfo();
                    updatePlayerAutoMoved(player, info);

                    this.cancel();
                }

                // mouvement sur X
                newLocation.setX(newLocation.getX() + offsetX);
                // mouvement sur Y
                newLocation.setY(newLocation.getY() + offsetY);
                // mouvement sur Z
                newLocation.setZ(newLocation.getZ() + offsetZ);

                newLocation.setPitch((float) (newLocation.getPitch() + offsetPitch));
                newLocation.setYaw((float) (newLocation.getYaw() + offetYaw));

                // mouvement de tete vers la droite
                // valeur possible:
                // -90(sud)
                // -180 (est)
                // 180 (ouest)
                // 90(orienté nord)
                // newLocation.setYaw((float) (newLocation.getYaw()+0.1));

                // mouvement de tete vers le bas, valeur compris entre -90 (vers
                // le haut du ciel)
                // et +90 tu mattes tes pieds
                // newLocation.setPitch((float) (newLocation.getPitch()+0.1));
                player2.teleport(newLocation);

            }

        }, 0, intervalDeTempsEntreLesTp * 1000);

    }

    public void cancelAnimate(Player player) {
        // TODO Auto-generated method stub
        t = this.getTimer(player, false);
        t.cancel();

        AutoMoveInfo info = new AutoMoveInfo();
        updatePlayerAutoMoved(player, info);
    }

    public void getInfoFor(Player playerMoved, Player player) {

        AutoMoveInfo info = this.getPlayerAutoMoved(playerMoved);

        if (info.getNbTotalSteps() > 1) {
            player.sendMessage(ChatColor.GREEN + "Il y a " + info.getNbTotalSteps() + " étapes à faire");
            player.sendMessage(ChatColor.GREEN + "On est à l'étape numéro " + info.getCurrentStep());
            player.sendMessage(ChatColor.GREEN + "Ce qui donne un pourcentage de réalisation de " + info.getCurrentPourcent());
        } else {
            player.sendMessage("Tu n'est pas en mode AutoMove");
        }

    }

    public void startRotateAnimation(final Player player, String dureTotale, String vitesseAngulaire) {

        // TODO Auto-generated method stub

        if (!this.playerCanBeAnimated(player)) {
            player.sendMessage("Commence par un /am start  et fini par un /am stop");
        }

        final double intervalDeTempsEntreLesTp = 1;
        final double LadurédelavidéoEnMinute = Double.parseDouble(dureTotale);

        // nombre d'étape
        final double nbEtapes = LadurédelavidéoEnMinute * 60 / intervalDeTempsEntreLesTp;

        AutoMoveInfo info = this.getPlayerAutoMoved(player);
        info.setNbTotalSteps((int) nbEtapes);
        this.updatePlayerAutoMoved(player, info);

        // Les positions
        final ArrayList<Location> positions = this.getLocations(player);
        final Location pointARegarder = positions.get(0);
        final Location pointEnd = positions.get(1);

        final Location pointCentre = new Location(pointARegarder.getWorld(), pointARegarder.getX(), pointEnd.getY(), pointARegarder.getZ());

        // picth ini
 
        System.out.println("pointARegarder " + pointARegarder);
        System.out.println("pointCentre " + pointCentre);
        System.out.println("pointEnd " + pointEnd);
          
        final double rayon = getNorme(pointEnd, pointCentre);
        double anglePitch;       
        
        anglePitch =  getAngleDansTriangleRectangle(pointEnd, pointCentre, pointARegarder);
        if(pointARegarder.getY() > pointEnd.getY())
        {
            anglePitch = -anglePitch;
        } 
         
        final double pitch = anglePitch ; 

        // Téléporte le player à la 1er coordonée
        Location pointSurLeCercle = new Location(pointEnd.getWorld(), pointEnd.getX(), pointEnd.getY(), pointEnd.getZ());

        // Calcul de la vitesse
        double vitesseInput = Double.parseDouble(vitesseAngulaire);

        // vitesseInput est le nombre de tour que l'on souhaite faire par minute
        final double vitesseRadSeconde = vitesseInput * 2 * Math.PI / 60;

        Location pointSurLeCerclePrime = new Location(pointARegarder.getWorld(), 0, 0, 0);
        pointSurLeCerclePrime.setX(pointSurLeCercle.getX() - pointCentre.getX());
        pointSurLeCerclePrime.setZ(pointSurLeCercle.getZ() - pointCentre.getZ());
        final double angleDepart = Math.atan(pointSurLeCerclePrime.getZ() / pointSurLeCerclePrime.getX());
        
          
        pointSurLeCercle.setPitch((float) pitch);
        pointSurLeCercle.setYaw((float) getYaw(pointEnd, pointARegarder));

        player.teleport(pointSurLeCercle);
        final Location newLocation = pointSurLeCercle;
 


        final Player player2 = player;

        t = this.getTimer(player, true);

        if(true)
        t.schedule(new TimerTask() {

            long nbCurrentEtape = (long) nbEtapes;
            double angle = (long) angleDepart;

            public void run() {

         //       System.out.println(" ");
        //        System.out.println(" ");
                nbCurrentEtape--;

                AutoMoveInfo info = getPlayerAutoMoved(player);
                info.setCurrentStep((int) (info.getNbTotalSteps() - nbCurrentEtape));
                updatePlayerAutoMoved(player, info);

                if (nbCurrentEtape <= 0) {
                    info = new AutoMoveInfo();
                    updatePlayerAutoMoved(player, info);

                    this.cancel();
                }
                
                double currentX = pointCentre.getX() + Math.cos(angle) * rayon;
                double curentZ = pointCentre.getZ() + Math.sin(angle) * rayon;

                // mouvement sur X
                newLocation.setX(currentX);

                // mouvement sur Z
                newLocation.setZ(curentZ);
 /*
                double test2;
                
                Vector v1 = getVectorFromPoint(newLocation, pointEnd);
                Vector v0 = getVectorFromPoint(player2.getLocation(), pointEnd);
                
                test2 = Math.toDegrees(v0.angle(v1));
                
                

                System.out.println("Ancien courant: " + v0);
                System.out.println("Point courant " + v1);
                System.out.println("test2 courant: " + test2);*/

                newLocation.setYaw((float) getYaw(newLocation, pointARegarder));
                 

                // newLocation.setPitch((float) (newLocation.getPitch() +
                // offsetPitch));
                // newLocation.setYaw((float) (newLocation.getYaw() +
                // offetYaw));

                // ///////////////
                // YAW
                // ///////////////
                // mouvement de tete vers la droite
                // valeur possible:
                // -90(sud)
                // -180 (est)
                // 0 (ouest)
                // 90(orienté nord)
                // newLocation.setYaw((float) (newLocation.getYaw()+0.1));

                // ///////////////
                // PITCH
                // ///////////////
                // mouvement de tete vers le bas, valeur compris entre -90 (vers
                // le haut du ciel)
                // et +90 tu mattes tes pieds
                // newLocation.setPitch((float) (newLocation.getPitch()+0.1));
                player2.teleport(newLocation);

                angle = angle + vitesseRadSeconde;


            }

        }, 0, (long) (1000));

    }
      
    private Vector getVectorFromPoint(Location a, Location b) {
       
        Vector v = new Vector(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
        return v;
    }

    /**
     * 
     * @return le norme du vecteur
     */
    public double getNorme(Location a, Location b){
        double distance = Math.sqrt(
                Math.pow(a.getX()-b.getX(), 2)  + 
                Math.pow(a.getY()-b.getY(), 2)  +
                Math.pow(a.getZ()-b.getZ(), 2));
        
        return Math.abs(distance);
    }
     

    public void debug(Player player) {
        // TODO Auto-generated method stub
        
    }
    
    public double getAngleDansTriangleRectangle(Location O, Location A, Location B)
    { 
        double adjacent = getNorme(O, A);
        double hypotenuse = getNorme(O, B);
        double anglePitch = Math.toDegrees( Math.acos( adjacent / hypotenuse) );
        return anglePitch;
         
    }
    
    public double getYaw(Location pointEnd, Location pointARegarder)
    {
        
        Vector vecteurVersOrigine = getVectorFromPoint( new Location(pointEnd.getWorld(),0, 0, 0),  new Location(pointARegarder.getWorld(), 0, 0, 1));
        Vector v0 = getVectorFromPoint( new Location(pointEnd.getWorld(), pointEnd.getX(), 0, pointEnd.getZ()),  new Location(pointARegarder.getWorld(), pointARegarder.getX(), 0, pointARegarder.getZ()));
        double test2 = Math.toDegrees(v0.angle(vecteurVersOrigine));
     //   System.out.println("Angle entre ces vecteurs " + test2); 
        
        double tx = v0.getX() - vecteurVersOrigine.getX();
        double tz = v0.getZ() - vecteurVersOrigine.getZ();
   //     System.out.println("diff X " + tx); 
    //    System.out.println("diff Z " + tz); 
        

        if(tz > 0 && tx > 0)
        {
       //     System.out.println(" ++ ?");
            test2 = -test2;
        } 
        

        if(tz < 0 && tx > 0)
        {
     //       System.out.println(" -+");
            test2 = -test2;
        }

        if(tz > 0 && tx < 0)
        {
   //         System.out.println(" negatif ?");
            test2 = test2;
        }
         
        
        if(tz < 0 && tx < 0)
        {
            System.out.println(" ?");
            test2 = test2;
        }
        
        return test2;
    }
    

}
