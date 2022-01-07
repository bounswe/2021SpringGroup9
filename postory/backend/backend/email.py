from djoser import email


class ActivationEmail(email.ActivationEmail):
    
    """
    Template path.
    """
    
    template_name = 'email/activation.html'
    
class ConfirmationEmail(email.ConfirmationEmail):
    
    """
    Template path.
    """
    
    template_name = 'email/confirmation.html'