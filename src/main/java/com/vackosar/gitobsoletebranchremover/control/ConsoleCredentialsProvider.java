package com.vackosar.gitobsoletebranchremover.control;

import com.google.inject.Singleton;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

@Singleton
public class ConsoleCredentialsProvider extends CredentialsProvider {

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public boolean supports(CredentialItem... items) {
        for (CredentialItem i : items) {
            if (! isSupported(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSupported(CredentialItem i) {
        return i instanceof CredentialItem.InformationalMessage
        || i instanceof CredentialItem.YesNoType
        || i instanceof CredentialItem.CharArrayType
        || i instanceof CredentialItem.StringType;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items)
            throws UnsupportedCredentialItem {
        for (CredentialItem i : items) {
            if (i instanceof CredentialItem.Username) {
                ((CredentialItem.Username) i).setValue(prompt(i));
            } else if (i instanceof CredentialItem.Password) {
                ((CredentialItem.Password) i).setValue(prompt(i).toCharArray());
            } else if (i instanceof CredentialItem.StringType) {
                ((CredentialItem.StringType) i).setValue(prompt(i));
            } else if (i instanceof CredentialItem.CharArrayType) {
                ((CredentialItem.CharArrayType) i).setValue(prompt(i).toCharArray());
            } else if (i instanceof CredentialItem.YesNoType) {
                ((CredentialItem.YesNoType) i).setValue(prompt((CredentialItem.YesNoType) i));
            } else if (i instanceof CredentialItem.InformationalMessage) {
                System.out.println(i.getPromptText());
            } else {
                throw new UnsupportedCredentialItem(uri, i.getClass().getName()+ ":" + i.getPromptText()); //$NON-NLS-1$
            }
        }
        return true;
    }

    private String prompt(CredentialItem i) {
        if (i.isValueSecure()) {
            return String.valueOf(System.console().readPassword(i.getPromptText() + ": "));
        } else {
            return String.valueOf(System.console().readLine(i.getPromptText() + ": "));
        }
    }
    
    private boolean prompt(CredentialItem.YesNoType item) {
        return Yn.valueOf(String.valueOf(System.console().readLine(item.getPromptText() + " [y/n]: "))).equals(Yn.y);
    }

    private enum Yn {
        y, n;
    }
}
