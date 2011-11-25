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

public class AutoMovePlugin extends JavaPlugin {

    private Timer t;
    
    HashMap<Player, Timer> timerPlayer;
    
    HashMap<Player, ArrayList<Location>> registerUser;

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

        getCommand("am").setExecutor(new  AutoMoveCommand(this, true)); 
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
                newLocation.setYaw((float) (newLocation.getYaw()+0.1));
                 
                // mouvement de tete vers le bas, valeur compris entre -90 (vers le haut du ciel)
                // et +90 tu mattes tes pieds
                newLocation.setPitch((float) (newLocation.getPitch()+0.1));
                player.teleport(newLocation);
                
            }

        }, 0, 1000);
        
    }

    public void stopTP(Player player, String[] args) {
        t.cancel();    
        t = new Timer();    
    }
    
    public ArrayList<Location> getLocations(Player player)
    {
        if(!(this.registerUser.get(player) != null))
        {
            this.registerUser.put(player, new ArrayList<Location>());
        } 

        return this.registerUser.get(player); 
    }
    
    public Timer getTimer(Player player, boolean b)
    {
        if(!(this.timerPlayer.get(player) != null))
        {
            this.timerPlayer.put(player, new Timer());    
        }
        
        Timer t = this.timerPlayer.get(player);
        
        if(b)
        {
            t = new Timer();
            this.timerPlayer.put(player, t);
        }
        
        return this.timerPlayer.get(player);
    }
    
    public boolean playerCanBeAnimated(Player player)
    {
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

    public void startLineAnimation(Player player, String intervalTp, String dureTotale) {
        // TODO Auto-generated method stub
        
        if(!this.playerCanBeAnimated(player))
        {
            player.sendMessage("Commence par un /am start  et fini par un /am stop");
        }
        
        final long intervalDeTempsEntreLesTp = Long.parseLong(intervalTp);
        final long LadurédelavidéoEnMinute = Long.parseLong(dureTotale);
       
        // nombre d'étape
        final long nbEtapes = LadurédelavidéoEnMinute * 60 / intervalDeTempsEntreLesTp;
        
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

        t  = this.getTimer(player, true);
         
        t.schedule(new TimerTask() { 

            long nbCurrentEtape = nbEtapes;

            public void run() {
                
                nbCurrentEtape--;
                
                if(nbCurrentEtape <= 0)
                {
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
                //newLocation.setYaw((float) (newLocation.getYaw()+0.1));
                 
                // mouvement de tete vers le bas, valeur compris entre -90 (vers le haut du ciel)
                // et +90 tu mattes tes pieds
                //newLocation.setPitch((float) (newLocation.getPitch()+0.1));
                player2.teleport(newLocation);
                
            }

        }, 0, intervalDeTempsEntreLesTp * 1000);
        
        
    }

    public void cancelAnimate(Player player) {
        // TODO Auto-generated method stub 
        t  = this.getTimer(player, false);      
        t.cancel();
    }

}
