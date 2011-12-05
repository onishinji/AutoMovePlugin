package onishinji.commands;

import java.util.Timer;
import java.util.TimerTask;

import onishinji.AutoMovePlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoMoveCommand implements CommandExecutor {

    private AutoMovePlugin plugin;
    private boolean isCommandStart;

    public AutoMoveCommand(AutoMovePlugin cameraManPlugin, boolean b) {
        // TODO Auto-generated constructor stub
        plugin = cameraManPlugin;
        
        isCommandStart = b;
    }

    @Override 
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO Auto-generated method stub
        
        if(!(sender instanceof Player))
        {
            return true;
        }
        
        final Player player = (Player) sender;
        
        if(args.length < 1)
        {
            player.sendMessage(ChatColor.RED + "usage: /am start");
            player.sendMessage(ChatColor.RED + "usage: /am stop");
            player.sendMessage(ChatColor.RED + "usage: /am line intervaleDeTempsEntreLesTpEnSeconde laDuréDeLaVidoEnMinute");
            
            return false;
        }
        
        String cmd = args[0];
       
        if(cmd.equals("start"))
        {
            plugin.registerFirstCoordinate(player);
            return true;
        }

        if(cmd.equals("stop"))
        {
            plugin.registerSecondCoordinate(player);
            return true;
        }
        

        if(cmd.equals("debug"))
        {
            player.sendMessage(player.getLocation().toString());
        }
        

        if(cmd.equals("cancel"))
        {
            plugin.cancelAnimate(player);
        }
        
        
        if(cmd.equals("info"))
        {
            String playerName = "";
            Player playerInfo = null;
            if(args.length == 2)
            {
                playerName = args[1];
            }
            else
            {
                playerName = player.getName();
            }
            
            for(Player currentP : player.getWorld().getPlayers())
            {
                if(currentP.getName().toLowerCase().equals(playerName.toLowerCase()))
                {
                    playerInfo = currentP;
                }
            }
            
            if(playerInfo != null)
            {
                plugin.getInfoFor(playerInfo, player);
            }
            
        }


        if(cmd.equals("line"))
        {
            if(args.length != 3)
            {
                player.sendMessage(ChatColor.RED + "Usage: /am line intervaleDeTempsEntreLesTpEnSeconde laDuréDeLaVidoEnMinute");
                return false;
            }
            
            String intervalTp = args[1];
            String dureTotale = args[2];
             
            plugin.startLineAnimation(player, intervalTp, dureTotale);
        }
        
       
    
        return true;
    }

}
