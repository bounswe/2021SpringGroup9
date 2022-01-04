from djoser import email


class ActivationEmail(email.ActivationEmail):
    template_name = 'email/activation.html'
    
class ConfirmationEmail(email.ConfirmationEmail):
    template_name = 'email/confirmation.html'