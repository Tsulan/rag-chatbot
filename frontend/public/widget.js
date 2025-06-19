(function() {
    if (document.getElementById('usm-chat-widget-iframe')) return; // Не вставлять дважды

    var iframe = document.createElement('iframe');
    iframe.id = 'usm-chat-widget-iframe';
    iframe.src = 'http://localhost:3000/widget-embed';
    iframe.style.position = 'fixed';
    iframe.style.bottom = '20px';
    iframe.style.right = '20px';
    iframe.style.width = '675px'; // ширина как в React-виджете
    iframe.style.height = '600px';
    iframe.style.border = 'none';
    iframe.style.borderRadius = '12px';
    iframe.style.zIndex = '10000';
    iframe.allow = 'clipboard-write';

    document.body.appendChild(iframe);
})(); 